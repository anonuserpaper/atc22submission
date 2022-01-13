package Anon.controller.core.topology

import Anon.controller.core.traffic.Flow
import Anon.controller.core.traffic.IP
import Anon.controller.core.traffic.IPPrefix
import Anon.controller.core.utils.getGzipReader
import java.util.*

/**
 * AS-level topology.
 * It takes a destination AS and a path toward traffic file as input to construct.
 *
 * - aggregate attacks based on certain granularity (seconds, minutes, hours)
 * - each file contains all information about the traffic in the corresponding time unit
 *      - time stamp
 *      - source IP
 *      - destination IP
 *      - volume (packets, octets)
 *      - other?
 * - AS-level topology takes the source and destination IPs and map them ASes
 * - for each source-destination AS pair, build a AS-level path and store it
 */
class AsTopology(var destinationAsn: Int = -1) {

    val ases = HashMap<Int, AS>()

    private val trie = TopologyTrie()

    private val tiers = HashMap<Int, Int>()     // asn to tier mapping

    private val asPathsCache: HashMap<Int, List<Int>>?

    init {
        println("Initializing AsTopology")
        if (destinationAsn > 0) {
            ases.put(destinationAsn, AS(destinationAsn, this, totalRuleSpace = 100))
            asPathsCache = loadPathsCacheGzip(destinationAsn)
        } else {
            asPathsCache = null
        }
        loadPrefixMapping()
        loadAsTierMapping()
    }

    /**
     * Load path from GZip file.
     * The data file is generated using CAIDA AS-relationship data.
     *
     * Format: from source to destination, ASN separated by ","
     */
    private fun loadPathsCacheGzip(destAsn: Int): HashMap<Int, List<Int>>? {
        // TODO: handle cases where path file does not exists.
        val br = getGzipReader("data/$destAsn-paths.txt.gz")
        val map = hashMapOf<Int, List<Int>>()
        br.readLines().forEach { line ->
            val path = line.split(",").asReversed().map { it.toInt() }
            map[path[0]] = path
        }
        println("\tlocal AS paths cache load success.")
        br.close()
        return map
    }

    private fun loadPrefixMapping() {
        // TODO: resolve hard-coded datafile
        println("\tloading rib table to maintain local prefix-AS mapping")
        val br = getGzipReader("data/rib-table-2016.csv.gz")
        br.readLines().forEach {
            val (prefix, asn) = it.trim().split(",")
            trie.addFlow(Flow(IP(prefix.split("/")[0])), prefix.split("/")[1].toInt(), asn.toInt())
        }
        br.close()
    }

    private fun loadAsTierMapping() {
        // TODO: resolve hard-coded datafile
        // TODO: dynamically extract AS tier information from input file.
        println("\tloading AS tier information")
        val br = getGzipReader("data/as-tiers.csv.gz")
        br.readLines().forEach {
            val (tier, asn) = it.trim().split(",").map { it.toInt() }
            tiers[asn] = tier
        }
        br.close()
    }

    /**
     * Given a IPPrefix, returns the list of ASes that own the prefix
     */
    fun queryAs(prefix: IPPrefix): Set<AS> {

        val asList = trie.getPrefixAsnSet(prefix).map { asn ->
            ases.putIfAbsent(asn, AS(asn, this, totalRuleSpace = 100))
            ases[asn]!!
        }

        return asList.toHashSet()
    }

    /**
     * a more efficient intersection function
     */
    private fun findIntersection(paths: List<List<AS>>): ArrayList<AS> {
        var index = 0
        val intersection = arrayListOf<AS>()
        while (true) {
            var common: AS? = paths[0].getOrNull(index) ?: break
            for (path in paths.drop(1)) {
                val cur = path.getOrNull(index)
                if (cur != common) {
                    common = null
                    break
                }
            }
            if (common == null)
                break
            else
                intersection.add(common)
            index++
        }
        return intersection
    }

    /**
     * Get the intersection of all paths from the given source ASes
     */
    fun getPathIntersection(ases: Set<AS>): List<AS> {
        val paths =
                ases.map { getAsPath(it).asReversed() }
                        .filter{it.isNotEmpty()}    // filter out unreachable sources

        return if (paths.isEmpty())
            listOf()
        else
            findIntersection(paths)
    }

    /**
     * shrink the tree to have at most ~limit~ number of leaves
     */
    fun getShrinkedLeaves(ases: Set<AS>, limit: Int): List<AS> {

        // if no need to shrink, return the input set as list
        if(ases.size<=limit){
            return ases.toList()
        }

        val paths =
                ases.map { getAsPath(it).asReversed() }
                        .filter{it.isNotEmpty()}    // filter out unreachable sources

        val allAses = paths.flatten().toSet()

        val selected = findIntersection(paths).toHashSet()

        while(true){
            val closest = selected.sortedBy { it.getDistance() }.first()
            val upstreams = closest.upstreamASes.filter{ it in allAses }

            if(upstreams.size + selected.size < limit){
                selected.addAll(upstreams)
            } else if (upstreams.size + selected.size ==  limit){
                selected.addAll(upstreams)
                return selected.toList()
            } else {
                selected.addAll(upstreams.take(limit - selected.size))
                return selected.toList()
            }
        }
    }

    fun getAsPath(sourceAs: AS): List<AS> {

        var asList: List<AS>
        //if (sourceAs.downstreamASes.size == 0) {
        if (!sourceAs.pathInferred) {
            // we have not yet queried the path for this AS

            /*
            if (asPathsCache == null) {
                // if we do not have cached AS paths, we use AS database to query
                asnList = AsPathDatabase.getPath(sourceAs.as_number, destAsn)
            } else {
                // if we have cached paths, we directly look it up
                asnList = asPathsCache[sourceAs.as_number]
            }
            */

            // TODO: handle cases where cache does not exists
            // TODO: produce path cache in advance
            val asnList: List<Int>? = asPathsCache!![sourceAs.as_number]

            asList = asnList?.map {
                ases.putIfAbsent(it, AS(it, this))
                ases[it]!!
            } ?: listOf()

            // build topology
            for ((index, asobject) in asList.withIndex()) {
                if (index != asList.lastIndex) {
                    val downstream = asList[index + 1]
                    asobject.downstreamASes.add(downstream)
                    downstream.upstreamASes.add(asobject)
                    asobject.pathInferred = true
                    downstream.pathInferred = true
                }
            }
        } else {
            // this AS was previously looked upon, or was on the path toward the victim from other ASes
            asList = arrayListOf()
            var cur = sourceAs
            asList.add(cur)
            while (cur.downstreamASes.size > 0) {
                cur = cur.downstreamASes.first()
                asList.add(cur)
            }
            if (cur.as_number != destinationAsn)
                asList = listOf()
        }

        return asList
    }
}


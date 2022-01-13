package Anon.controller.core.topology

import Anon.controller.core.traffic.Flow
import Anon.controller.core.traffic.IP
import Anon.controller.core.traffic.IPPrefix


private class TopologyNode(// only visible within this file
        var parent: TopologyNode? = null, var leftChild: TopologyNode? = null, var rightChild: TopologyNode? = null,
        val depth: Int, val leftRight: Int = -1, // left:0, right:1, root:-1
        var asn: Int = -1, val asSet: HashSet<Int> = HashSet()
) {

    var selected = false
}

class TopologyTrie {

    private val root = TopologyNode(depth = 0)

    private val depthMap = hashMapOf<Int, ArrayList<TopologyNode>>()

    init {
        (0..32).forEach {
            depthMap.put(it, arrayListOf())
        }

        depthMap[0]!!.add(root)
    }

    /**
     * isBad:
     *  * 1: bad
     *  * 0: good
     *  * -1: unknown
     */
    fun addFlow(flow: Flow, prefixDepth: Int = -1, asn: Int = -1) {
        var currentNode = root
        val nodes = arrayListOf<TopologyNode>()
        nodes.add(currentNode)

        val ipLong = flow.ip.toLong()

        (31 downTo 0).forEach {
            val depth = 32 - it
            val bitSet = (ipLong shr it and 0x01) == 1.toLong()

            if (bitSet) {
                if (currentNode.rightChild == null) {
                    val newnode = TopologyNode(currentNode, depth = depth, leftRight = 1)
                    depthMap[depth]!!.add(newnode)
                    currentNode.rightChild = newnode
                }
                currentNode = currentNode.rightChild!!

            } else {
                if (currentNode.leftChild == null) {
                    val newnode = TopologyNode(currentNode, depth = depth, leftRight = 0)
                    depthMap[depth]!!.add(newnode)
                    currentNode.leftChild = newnode
                }
                currentNode = currentNode.leftChild!!
            }

            if (depth == prefixDepth) {
                currentNode.selected = true
                currentNode.asn = asn
                currentNode.asSet.add(asn)
            }
            nodes.add(currentNode)
        }

        nodes.forEach { it.asSet.add(asn) }
    }

    /**
     * Using the trie structure, the problem translates to given a node, return a list of nodes
     * if the corresponding node is selected, return a list contains only the corresponding node
     * otherwise, if it has descendent nodes, return all decendent nodes, else return the closest ancestor node
     */
    fun getPrefixAsnSet(prefix: IPPrefix, onlyAncestor: Boolean = true): Set<Int> {
        var currentNode = root

        val prefixDepth = prefix.getMaskInt()

        if (prefixDepth == 0)
            return currentNode.asSet


        val ancestorNodes = arrayListOf<TopologyNode>()

        var inTrie = true

        val ipLong = IP(prefix.getLowIpString()).toLong()

        for (shift in (31 downTo 0)) {
            val depth = 32 - shift
            val bitSet = (ipLong shr shift and 0x01) == 1.toLong()

            if (bitSet) {
                if (currentNode.rightChild == null) {
                    inTrie = false
                    break
                }
                currentNode = currentNode.rightChild!!
            } else {
                if (currentNode.leftChild == null) {
                    inTrie = false
                    break
                }
                currentNode = currentNode.leftChild!!
            }

            if (depth == prefixDepth) {
                if (!currentNode.selected) {
                    break
                } else {
                    return setOf(currentNode.asn)
                }
            } else if (depth < prefixDepth && currentNode.selected) {
                ancestorNodes.add(currentNode)
            }
        }

        if (inTrie) {
            //val descendentNodes = getDescendentNodes(currentNode)
            val descendentNodes = currentNode.asSet

            if (descendentNodes.isEmpty()) {
                return setOf(ancestorNodes.sortedBy { it.depth }.last().asn)
            } else {
                return descendentNodes
            }
        } else {
            if (ancestorNodes.isNotEmpty()) {
                return setOf(ancestorNodes.sortedBy { it.depth }.last().asn)
            } else {
                return setOf()
            }
        }
    }
}


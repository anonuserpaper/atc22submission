package Anon.client.rulegeneration.core

import Anon.client.rulegeneration.core.profiles.RuleGenerationProfile
import Anon.client.rulegeneration.core.profiles.Strategy
import Anon.client.rulegeneration.core.resource.Flow
import Anon.client.rulegeneration.core.ftree.FTree
import Anon.client.rulegeneration.core.ftree.TreeNode
import java.util.Arrays.asList


/**
 * Rule generation algorithm
 */
class RuleGenerationTree : FTree() {

    /**
     * Insert flows into an existing tree, building up the tree structure.
     * NOTE: each insertion also marks the node as selected, but the tree will be pruned at the beginning of rule generation procedure
     *
     * @param unwanted List of unwanted flows
     * @param protected List of protected flows
     */
    fun insert(unwanted: List<Flow>, protected: List<Flow>, time: Int = 0) {
        unwanted.forEach {
            this.addFlow(it, time, isBad = true)
        }
        protected.forEach {
            this.addFlow(it, time, isBad = false)
        }
        println("Inserted ${unwanted.size + protected.size} flows.")
    }

    fun generateRules(unwanted: List<Flow> = emptyList(), protected: List<Flow> = emptyList(), profile: RuleGenerationProfile, time: Int): List<String> {
        if (unwanted.size + protected.size > 0) {
            this.insert(unwanted, protected, time)
        }

        when (profile.strategy) {
            Strategy.MAXCOVERAGE -> this.maxCoverage(profile, time)
            Strategy.MINCOLLATERAL -> this.minCollateral(profile, time)
            Strategy.MINRULES -> this.minRules(profile, time)
        }
        return this.getSelectedNodes().map { it.nodePrefix.prefix }
    }

    /**
     * Generate rules from bottom up using greedy algorithm.
     * for all pairs of neighboring nodes, selecting the ancestor of which has the smallest amount of collateral damage increase
     */
    fun minCollateral(profile: RuleGenerationProfile, time: Int = 0) {

        println("Minimize collateral: B = ${profile.minCoverage}, M = ${profile.maxRules}")

        val sort = { node: TreeNode -> node.counts.badFlows }

        var nodeList: List<TreeNode>
        while (true) {

            nodeList = this.getSelectedNodes().sortedBy { node -> node.sumBadFlows(time) }.asReversed()

            if (nodeList.size <= profile.maxRules) {
//                println("\t\t\tFIRST CONDITION")
                break
            } else if (nodeList.take(profile.maxRules).sumBy { node -> node.sumBadFlows(time) } >= profile.minCoverage) {
//                println("\t\t\tSECOND CONDITION")
                nodeList.drop(profile.maxRules).forEach { it.deselect() }
                break
            }

            // get a list of nodes sorted by their spatial location
            nodeList = nodeList.sortedBy { it.ipValue }

            val ancestors = (0..(nodeList.size - 2)).map { i ->
                val node1 = nodeList[i]
                val node2 = nodeList[i + 1]

                // get common ancestor
                val ancestor = this.getCommonAncestor(node1, node2)
                val gDiff = ancestor.sumGoodFlows(time) - node1.sumGoodFlows(time) - node2.sumGoodFlows(time)
                Pair(ancestor, gDiff)
            }

            // Get the smallest G value for all potential aggregations
            val minGValue = ancestors.sortedBy { it.second }.first().second
            // Choose the aggregation that has the smallest G value, using the tree depth as a tiebreaker
            val aggr = ancestors.filter { it.second == minGValue }.sortedBy { it.first.depth }.last().first
            // Perform the selected aggregation and prune the tree
            aggr.select()
            this.pruning(asList(aggr))
        }
        // Clear all deselected nodes
        this.finishNodeSelectionChanges()
    }

    /**
     * Generate rules from bottom up using greedy algorithm.
     * for all pairs of neighboring nodes, selecting the ancestor of which has the largest amount of coverage
     */

    fun maxCoverage(profile: RuleGenerationProfile, time: Int = 0) {

        println("Maximize coverage: G = ${profile.maxCollateral}, M = ${profile.maxRules}")

        var selectedNodes = this.getSelectedNodes()

        // compress tree to a concise form first
        // this step is O(n)
        selectedNodes.forEach { node ->
            val goodFlows = node.counts.goodFlows

            var tempNode = node.parent
            var targetNode: TreeNode? = null
            while (tempNode != null && tempNode.counts.goodFlows == goodFlows) {
                targetNode = tempNode
                tempNode = tempNode.parent
            }
            if (targetNode != null && targetNode != node) {
                targetNode.select()
                node.deselect()
            }
        }
        this.pruning()

        // in worst case, this loop runs n times
        while (true) {
            // first, we sort the selected nodes by their IP value
            // this could be faster if we modify the tree to return the sorted list
            // would perhaps reduce complexity for this sort from O(n*logn) to O(n)
            var nodeList = this.getSelectedNodes().toList().sortedBy { it.ipValue }

            val gNodes = nodeList.filter { it.sumGoodFlows(time) > 0 }

            val gTotal = gNodes.sumBy { it.sumGoodFlows(time) }

            // here, if the number of selected nodes is less than maxRules,
            // we are done.
//            if (nodeList.size <= profile.maxRules) { break }

            // get common ancestors for all consecutive nodes
            // this step is O(n)
            val ancestors = (0..(nodeList.size - 2)).map { i ->
                val node1 = nodeList[i]
                val node2 = nodeList[i + 1]

                // return common ancestor
                val ancestor = this.getCommonAncestor(node1, node2)
                val gSum = gNodes.filter { !ancestor.covers(it) }.sumBy { it.sumGoodFlows(time) } + ancestor.sumGoodFlows(time)
                Pair(ancestor, gSum)
            }

            // sort all ancestor by the increase of collateral damage
            // this step is O(n)
            val aggr = ancestors
                    .filter { it.second <= profile.maxCollateral }
                    .sortedBy { it.first.sumBadFlows(time) }
                    .lastOrNull()?.first ?: break

            aggr.select()
//            println("Agg: g = ${aggr.counts.goodFlows + gTotal}")
            this.pruning(asList(aggr))

        }

        this.getSelectedNodes().sortedBy { it.sumBadFlows(time) }.asReversed().drop(profile.maxRules).forEach { it.deselect() }
        this.finishNodeSelectionChanges()
    }


    fun minRules(profile: RuleGenerationProfile, time: Int = 0) {

        println("Minimize rules: G = ${profile.maxCollateral}, B = ${profile.minCoverage}")

        var selectedNodes = this.getSelectedNodes()

        // compression step
        // this step is O(n)
        selectedNodes.forEach { node ->
            val goodFlows = node.counts.goodFlows

            var tempNode = node.parent
            var targetNode: TreeNode? = null
            while (tempNode != null && tempNode.counts.goodFlows == goodFlows) {
                targetNode = tempNode
                tempNode = tempNode.parent
            }
            if (targetNode != null && targetNode != node) {
                targetNode.select()
                node.deselect()
            }
        }
        this.pruning()

        val bMin = profile.minCoverage
        val gMax = profile.maxCollateral

        while (true) {
            var nodeList = this.getSelectedNodes().toList().sortedBy { it.ipValue }

            val ancestors = (0..(nodeList.size - 2)).map { i ->
                val node1 = nodeList[i]
                val node2 = nodeList[i + 1]

                val ancestor = this.getCommonAncestor(node1, node2)
                val part = nodeList.partition { ancestor.covers(it) }
                val deltaRules = part.first.size
                val gNew = part.second.sumBy { it.sumGoodFlows(time) } + ancestor.sumBadFlows(time)
                val bNew = part.second.sumBy { it.sumBadFlows(time) } + ancestor.sumBadFlows(time)

                data class ReturnVals(val ancestor: TreeNode, val deltaRules: Int, val gNew: Int, val bNew: Int)

                ReturnVals(ancestor, deltaRules, gNew, bNew)
            }.sortedBy { it.deltaRules }//.asReversed()

            if (ancestors.isEmpty()) break

            for (ancestor in ancestors) {
                if (ancestor.gNew <= gMax && ancestor.bNew >= bMin) {
                    ancestor.ancestor.select()
                    this.pruning(asList(ancestor.ancestor))
                    break
                }
            }

            if (this.getSelectedNodes().size == nodeList.size) break
        }

        var preFinalNodes = this.getSelectedNodes().toList().sortedBy { it.sumBadFlows(time) }

        var bSum = preFinalNodes.sumBy { it.sumBadFlows(time)}

        for (node in preFinalNodes) {
            if ((bSum - node.sumBadFlows(time)) >= bMin) {
                node.deselect()
                bSum -= node.sumBadFlows(time)
            } else break
        }
        this.finishNodeSelectionChanges()
//        println("Terminating with ${this.getSelectedNodes().size} node(s)")
    }
}

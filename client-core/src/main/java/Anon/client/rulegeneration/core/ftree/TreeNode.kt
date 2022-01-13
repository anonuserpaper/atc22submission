package Anon.client.rulegeneration.core.ftree


import Anon.client.rulegeneration.core.resource.FilteringRule
import Anon.client.rulegeneration.core.resource.Flow
import Anon.client.rulegeneration.core.resource.IPPrefix
import Anon.client.rulegeneration.core.utils.IpUtils
import kotlin.math.roundToInt

class FlowCount {
        var badFlows: Pair<Int, Int> = Pair(0, 0)
        var goodFlows: Pair<Int, Int> = Pair(0, 0)
        var badVol: Pair<Int, Int> = Pair(0, 0)
        var goodVol: Pair<Int, Int> = Pair(0, 0)
        var lastUpdate: Int = 0
}

class TreeNode(
        val tree: FTree,
        val depth: Int,
        var parent: TreeNode? = null,
        var leftChild: TreeNode? = null,
        var rightChild: TreeNode? = null,
        val leftRight: Int = -1, // left:0, right:1, root:-1
        var counts: FlowCount = FlowCount()
) {
    val nodePrefix: IPPrefix

    var aggregated = false

    var ipValue: Long = 0

    val alpha = 0.5

    private var selected = false

    private val nodeRule: FilteringRule

    init {
        nodePrefix = getPrefix()
        nodeRule = FilteringRule(nodePrefix.toString(), "")
    }

    override fun toString(): String {
        return "TreeNode(depth=$depth, left|right=$leftRight, $nodePrefix)"
    }

    fun select() {
        selected = true
        tree.addSelectedNode(this)
    }

    fun deselect() {
        selected = false
        tree.removeSelectedNode(this)
    }

    fun isSelected(): Boolean {
        return selected
    }

    private fun getPrefix(): IPPrefix {
        val path = arrayListOf<Char>()
        var currentNode = this
        while (currentNode.parent != null) {
            if (currentNode.leftRight == 0) {
                path.add('0')
            } else {
                path.add('1')
            }
            currentNode = currentNode.parent!!
        }

        ipValue = 0

        var tempDepth = this.depth
        for (bitChar in path) {
            if (bitChar == '1')
                ipValue += Math.pow(2.0, 32.0 - tempDepth).toLong()
            tempDepth--
        }

        return IPPrefix(IpUtils.longToIP(ipValue) + "/${this.depth}")
    }

    /**
     * Update flow count information when inserting flows to the tree
     */
    fun updateCounts(flow: Flow, time: Int, isBad: Boolean) {
        if (isBad) {
            if (time == this.counts.lastUpdate) {
                this.counts.badFlows = Pair(this.counts.badFlows.first + 1, this.counts.badFlows.second)
            } else {
                val avg = (alpha * this.counts.badFlows.first) + ((1 - alpha) * this.counts.badFlows.second)
                this.counts.badFlows = Pair(1, avg.roundToInt())
                this.counts.lastUpdate = time
            }
        } else {
            if (time == this.counts.lastUpdate) {
                this.counts.goodFlows = Pair(this.counts.goodFlows.first + 1, this.counts.goodFlows.second)
            } else {
                val avg = (alpha * this.counts.goodFlows.first) + ((1 - alpha) * this.counts.goodFlows.second)
                this.counts.goodFlows = Pair(1, avg.roundToInt())
                this.counts.lastUpdate = time
            }
        }
    }

    fun sumBadFlows(time: Int): Int {
        if (time == this.counts.lastUpdate) {
            val avg = (alpha * this.counts.badFlows.first) + ((1 - alpha) * this.counts.badFlows.second)
            return avg.roundToInt()
        } else {
            val avg = ((1 - alpha) * this.counts.badFlows.second)
            return avg.roundToInt()
        }
    }

    fun sumGoodFlows(time: Int): Int {
        if (time == this.counts.lastUpdate) {
            val avg = (alpha * this.counts.goodFlows.first) + ((1 - alpha) * this.counts.goodFlows.second)
            return avg.roundToInt()
        } else {
            val avg = ((1 - alpha) * this.counts.goodFlows.second)
            return avg.roundToInt()
        }
    }

    /**return
     * Determines if the current node covers another target node.
     */
    fun covers(target: TreeNode): Boolean {
        if (target == this)
            return true

        var cur = target
        while (cur.parent != null) {
            cur = cur.parent!!
            if (cur == this)
                return true
        }
        return false
    }

    /**
     * Check if a node is covered by any aggregated nodes from 2-phase aggregation.
     */
    fun coveredByAggr(): TreeNode? {
        var cur = this

        while (cur.parent != null) {
            cur = cur.parent!!
            if (cur.aggregated)
                return cur
        }
        return null
    }

}

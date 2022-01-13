package Anon.client.rulegeneration.core.ftree

import Anon.client.rulegeneration.core.resource.Flow
import Anon.client.rulegeneration.core.resource.IP
import Anon.client.rulegeneration.core.utils.IpUtils


open class FTree {

    val root = TreeNode(this, depth = 0)

    private val depthMap = hashMapOf<Int, ArrayList<TreeNode>>()

    private val selectedNodes = hashSetOf<TreeNode>()

    private val toAddNodes = hashSetOf<TreeNode>()

    private val toRemoveNodes = hashSetOf<TreeNode>()

    init {
        (0..32).forEach { depthMap.put(it, arrayListOf())}
        depthMap[0]!!.add(root)
    }

    val addSelectedNode = toAddNodes::add

    val removeSelectedNode = toRemoveNodes::add

    /**
     * Add flows to the tree.
     *
     * isBad:
     *  * 1: bad
     *  * 0: good
     *  * -1: unknown
     */
    fun addFlow(flow: Flow, time: Int, isBad: Boolean) {
        var currentNode = root
        val nodes = arrayListOf<TreeNode>()
        nodes.add(currentNode)

        val ipLong = IpUtils.ipToLong(flow.source)

        (31 downTo 0).forEach { level ->
            val depth = 32 - level
            if ((ipLong shr level and 0x01) == 1.toLong()) {
                if (currentNode.rightChild == null) {
                    val newNode = TreeNode(this, parent = currentNode, depth = depth, leftRight = 1)
                    depthMap[depth]!!.add(newNode)
                    currentNode.rightChild = newNode
                }
                currentNode = currentNode.rightChild!!
            } else {
                if (currentNode.leftChild == null) {
                    val newNode = TreeNode(this, parent = currentNode, depth = depth, leftRight = 0)
                    depthMap[depth]!!.add(newNode)
                    currentNode.leftChild = newNode
                }
                currentNode = currentNode.leftChild!!
            }
            nodes.add(currentNode)
        }

        // NOTE: always add new flow rules as selected when inserting. the overlapping rules will be unselected during rule generation.
        if (isBad) nodes.last().select()

        nodes.forEach { node -> node.updateCounts(flow, time, isBad) }
    }

    /**
     * Get the common ancestor of two nodes on the tree.
     * Worst case scenario, the root node will be returned
     */
    fun getCommonAncestor(node1: TreeNode, node2: TreeNode): TreeNode {
        val ipvalue1 = node1.ipValue
        val ipvalue2 = node2.ipValue
        var ancestor = this.root

        for (i in (31 downTo 0)) {
            val bitSet1 = (ipvalue1 shr i and 0x01) == 1.toLong()
            val bitSet2 = (ipvalue2 shr i and 0x01) == 1.toLong()

            if (bitSet1 != bitSet2 || ancestor == node1 || ancestor == node2) {
                break
            }
            try {
                if (bitSet1) {
                    ancestor = ancestor.rightChild!!
                } else {
                    ancestor = ancestor.leftChild!!
                }
            } catch(e: KotlinNullPointerException){
                error(e.message!!)
            }
        }

        // now they're the same node
        return ancestor
    }

    /**
     * Prune overlapping rules on the tree.
     */
    fun pruning(nodes: List<TreeNode> = listOf()): HashSet<TreeNode> {
        if (nodes.isNotEmpty()) {
            nodes.forEach { node ->
                if (node.isSelected()) { // NOTE: no need to prune if already pruned
                    pruneRecursive(node)
                    finishNodeSelectionChanges()
                }
            }
        } else {
            this.getSelectedNodes().forEach { node ->
                if (node.isSelected())
                    pruneRecursive(node)
            }
        }
        return this.getSelectedNodes()
    }

    private fun pruneRecursive(node: TreeNode) {
        val left = node.leftChild
        val right = node.rightChild
        left?.let {
            left.deselect()
            pruneRecursive(left)
        }
        right?.let {
            right.deselect()
            pruneRecursive(right)
        }
    }

    fun getSelectedNodes(): HashSet<TreeNode> {
        finishNodeSelectionChanges()
        return selectedNodes
    }

    fun finishNodeSelectionChanges() {
        selectedNodes.removeAll(toRemoveNodes)
        selectedNodes.addAll(toAddNodes)
        toRemoveNodes.clear()
        toAddNodes.clear()
    }

    fun resetSelectedNodes(nodes: Set<TreeNode>) {
        // clear old rules
        selectedNodes.forEach { it.deselect() }
        selectedNodes.clear()
        toRemoveNodes.clear()
        toAddNodes.clear()

        // add new rules
        nodes.forEach {
            it.select()
            selectedNodes.add(it)
        }
    }

    /**
     * Check if the current tree covers the IP
     */
    fun covers(ip: IP): Boolean {
        var currentNode = root

        val ipLong = ip.toLong()

        (31 downTo 0).forEach {
            if (currentNode.isSelected()) return true

            val bitSet = (ipLong shr it and 0x01) == 1.toLong()

            if (bitSet) {
                if (currentNode.rightChild == null) {
                    return false
                }
                currentNode = currentNode.rightChild!!
            } else {
                if (currentNode.leftChild == null) {
                    return false
                }
                currentNode = currentNode.leftChild!!
            }
        }

        return currentNode.isSelected()
    }


    /**
     * filter the flows using the tree
     *
     * @return a pair of list of flows, the first one are filtered flows, and the second one are unfiltered.
     */
    fun filterFlows(flows: List<Flow>): Pair<List<Flow>, List<Flow>> {
        return flows.partition { flow -> this.covers(IP(flow.source)) }
    }



    fun getNodes(ip: IP): List<TreeNode> {
        val res = hashSetOf<TreeNode>()
        var currentNode = root

        val ipLong = ip.toLong()

        //(31 downTo 0).forEach {
        for (shift in (31 downTo 0)) {

            if (currentNode.isSelected()) res.add(currentNode)
            if ((ipLong shr shift and 0x01) == 1.toLong()) {
                if (currentNode.rightChild == null) {
                    break
                }
                currentNode = currentNode.rightChild!!
            } else {
                if (currentNode.leftChild == null) {
                    break
                }
                currentNode = currentNode.leftChild!!
            }
        }

        if (currentNode.isSelected()) res.add(currentNode)

        return res.toList()
    }
}
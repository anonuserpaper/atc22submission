package Anon.controller.core.topology

import Anon.controller.core.traffic.FilteringRule
import Anon.controller.core.traffic.Flow
import java.util.*

/**
 * AS class describes the basic parameters each AS has.
 */

data class AS(
        val as_number: Int,
        val topology: AsTopology,
        val upstreamASes: HashSet<AS> = HashSet(),
        val downstreamASes: HashSet<AS> = HashSet(),
        var pathInferred: Boolean = false,
        var totalRuleSpace: Int = 0,
        private var deployedRules: HashSet<FilteringRule> = HashSet()
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AS)
            this.as_number == other.as_number
        else
            false
    }

    override fun hashCode(): Int = as_number

    override fun toString(): String = "AS${this.as_number}, available rule space ${totalRuleSpace - deployedRules.size}"

    fun getAvailableSpace() = totalRuleSpace - deployedRules.size

    fun getPath(): List<AS> {

        val path = arrayListOf<AS>()
        var current = this
        while (current.downstreamASes.size > 0) {
            path.add(current)
            current = current.downstreamASes.first()
        }
        return path
    }

    fun getDistance(): Int {
        // topology.getAsPath(this, destAsn)
        var distance = 0
        var current = this
        while (current.downstreamASes.size > 0) {
            current = current.downstreamASes.first()
            distance++
        }
        return distance
    }
}

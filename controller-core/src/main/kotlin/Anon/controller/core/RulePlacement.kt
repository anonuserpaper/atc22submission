package Anon.controller.core

import Anon.controller.core.algorithms.PlacementAlgorithm
import Anon.controller.core.algorithms.PlacementCutSet
import Anon.controller.core.algorithms.PlacementPerRule
import Anon.controller.core.topology.AS
import Anon.controller.core.topology.AsTopology
import Anon.controller.core.traffic.FilteringRule

class RulePlacement(asn: Int, algorithmType: String){

    private val topology: AsTopology = AsTopology(asn)

    private val algorithm: PlacementAlgorithm

    init {
        algorithm = when(algorithmType.toLowerCase()){
            "perrule" -> PlacementPerRule(topology)
            "cutset" -> PlacementCutSet(topology)
            else -> PlacementPerRule(topology)
        }
    }

    fun findPlacement(rules: List<FilteringRule>, numAsLimit: Int): Map<FilteringRule, List<AS>>? {
        return algorithm.findPlacement(rules, numAsLimit)
    }
}

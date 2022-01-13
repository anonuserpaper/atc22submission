package Anon.controller.core.algorithms

import Anon.controller.core.topology.AS
import Anon.controller.core.traffic.FilteringRule

abstract class PlacementAlgorithm{

    abstract fun findPlacement(rules: List<FilteringRule>, maxAsesPerRule: Int): Map<FilteringRule, List<AS>>?
}

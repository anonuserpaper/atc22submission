package Anon.controller.core.algorithms

import Anon.controller.core.topology.AS
import Anon.controller.core.topology.AsTopology
import Anon.controller.core.traffic.FilteringRule

class PlacementPerRule( private val topology: AsTopology ): PlacementAlgorithm()
{

    override fun findPlacement(rules: List<FilteringRule>, maxAsesPerRule: Int):
            Map<FilteringRule, List<AS>>? {

        if(rules.isEmpty()){
            return null
        }
        assert(rules.isNotEmpty())

        val rulePlacementMap = hashMapOf<FilteringRule, List<AS>>()

        // calling path inference
        val rulePathMap: Map<FilteringRule, List<AS>> = getPathRuleMap(rules)

        // fetch available rulespace
        // ASes in topology won't be created until path was queried.
        val asRuleSpaceMap = hashMapOf<Int, Int>()
        rulePathMap.forEach{ _, path ->
            path.forEach{ asptr ->
                asRuleSpaceMap[asptr.as_number] = asptr.getAvailableSpace()
            }
        }

        // sorted by priority
        val sortedRules = rulePathMap.keys.sortedBy { it.priority }.asReversed()
        for(rule in sortedRules){
            val path = rulePathMap[rule]
            if(path==null){
                continue
            } else {
                for(asptr in path) {
                    if(asRuleSpaceMap[asptr.as_number]!!>0){
                        asRuleSpaceMap[asptr.as_number] = asRuleSpaceMap[asptr.as_number]!! - 1
                        rulePlacementMap[rule] = listOf(asptr)
                        break
                    }
                }
            }
        }

        return rulePlacementMap
    }

    private fun getPathRuleMap(rules: List<FilteringRule>): Map<FilteringRule, List<AS>> {
        val rulePathMap = hashMapOf<FilteringRule, List<AS>>()

        for(rule in rules){
            val ownerAses = topology.queryAs(rule.sourcePrefix)
            val path = topology.getPathIntersection(ownerAses)
            rulePathMap[rule] = path
        }

        return rulePathMap
    }
}
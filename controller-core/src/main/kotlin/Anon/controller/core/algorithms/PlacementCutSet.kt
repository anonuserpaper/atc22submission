package Anon.controller.core.algorithms

import Anon.controller.core.topology.AS
import Anon.controller.core.topology.AsTopology
import Anon.controller.core.traffic.FilteringRule

class PlacementCutSet( private val topology: AsTopology ): PlacementAlgorithm(){

    override fun findPlacement(rules: List<FilteringRule>, maxAsesPerRule: Int):
            Map<FilteringRule, List<AS>>? {

        if(rules.isEmpty()){
            return null
        }
        assert(rules.isNotEmpty())

        val rulePlacementMap = hashMapOf<FilteringRule, ArrayList<AS>>()

        // calling path inference
        // val rulePathMap: Map<FilteringRule, List<AS>> = getPathRuleMap(rules)
        val rulePathsMap = hashMapOf<FilteringRule, List<List<AS>>>()

        for(rule in rules){
            val ownerAses = topology.queryAs(rule.sourcePrefix)
            val shrinkedLeaves = topology.getShrinkedLeaves(ownerAses, maxAsesPerRule)
            rulePathsMap[rule] = shrinkedLeaves.map{ it.getPath()}
        }



        // fetch available rulespace
        // ASes in topology won't be created until path was queried.
        val asRuleSpaceMap = hashMapOf<Int, Int>()
        rulePathsMap.forEach{ _, paths ->
            paths.forEach{path ->
                path.forEach{ asptr ->
                    asRuleSpaceMap[asptr.as_number] = asptr.getAvailableSpace()
                }
            }
        }

        // sorted by priority
        val sortedRules = rulePathsMap.keys.sortedBy { it.priority }.asReversed()
        for(rule in sortedRules){
            val paths = rulePathsMap[rule]!!

            paths.forEach{path ->

                for(asptr in path) {
                    if(asRuleSpaceMap[asptr.as_number]!!>0){
                        asRuleSpaceMap[asptr.as_number] = asRuleSpaceMap[asptr.as_number]!! - 1
                        // add to list, create new list if first time
                        rulePlacementMap.getOrDefault(rule, arrayListOf()).add(asptr)
                        break
                    }
                }

            }
        }

        return rulePlacementMap
    }

    private fun getPathRuleMap(rules: List<FilteringRule>, maxAsesPerRule: Int): Map<FilteringRule, List<AS>> {
        val rulePathMap = hashMapOf<FilteringRule, List<AS>>()

        for(rule in rules){
            val ownerAses = topology.queryAs(rule.sourcePrefix)
            val path = topology.getPathIntersection(ownerAses)
            rulePathMap[rule] = path
        }

        return rulePathMap
    }

}

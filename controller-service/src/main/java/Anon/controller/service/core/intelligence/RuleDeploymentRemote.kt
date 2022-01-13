package Anon.controller.service.core.intelligence

import Anon.controller.core.RulePlacement
import Anon.controller.core.traffic.FilteringRule
import Anon.controller.service.core.rule.FlowRule
import java.util.*

/**
 * Rule deployment algorithm for remote deployment:
 * given a list of AS paths of the flows to be filtered,
 * find out the best locations for the deployment of these flows.
 *
 *
 * The current algorithm is based on the ChokePoint algorithm described in
 * the modeling of the DDoS defense algorithms work.
 */
class RuleDeploymentRemote {

    val rulePlacement = RulePlacement(15169, "PerRule")

    /**
     * Given a AnonRule, decide which AS the rules should go to for each FlowRule inside.

     * @param flowRules list of FlowRule object
     */
    fun setDeploymentLocations(flowRules: List<FlowRule>): HashMap<FlowRule, List<Int>> {

        val ruleDeploymentMap = HashMap<FlowRule, List<Int>>()

        // translate FlowRule to FilteringRule
        val ruleTranslateMap = flowRules.map { FilteringRule(it.flowFields["nw_src"]!!, it.flowFields["nw_dst"]!!) to it }.toMap()
        val deploymentMap = rulePlacement.findPlacement(ruleTranslateMap.keys.toList(), 100) ?: mapOf()

        for ((k, v) in deploymentMap) {
            val flowRule = ruleTranslateMap[k]
            if (flowRule != null) {
                ruleDeploymentMap[flowRule] = v.map { it.as_number }
            }
        }

        return ruleDeploymentMap
    }
}

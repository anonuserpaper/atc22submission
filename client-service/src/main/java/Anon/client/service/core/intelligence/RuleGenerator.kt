package Anon.client.service.core.intelligence

import Anon.client.rulegeneration.core.RuleGenerationTree
import Anon.client.rulegeneration.core.profiles.RuleGenerationProfile
import Anon.client.service.core.resource.TrafficMessage
import Anon.client.service.utils.RuleUtils
import Anon.client.service.message.client.ClientCreateRuleMsg
import Anon.client.service.message.client.ClientDeleteFlowRuleMsg
import org.glassfish.jersey.jackson.JacksonFeature

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity

import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 *
 * The RuleGenerator class takes in flows from TrafficHandler and processes them.
 *
 */

class RuleGenerator
/**
 * private constructor
 * make sure only one instance will be created
 */
private constructor() {

    var ruleGenTree: RuleGenerationTree = RuleGenerationTree()
    var deployedRuleMap: HashMap<String, String> = HashMap()
    var ruleGenerationProfile = RuleGenerationProfile()
    var providerAddress = ""

    fun init(ruleGenerationProfile: RuleGenerationProfile, providerAddress: String) {
        this.ruleGenerationProfile = ruleGenerationProfile
        this.providerAddress = providerAddress
    }

    fun addNewFlows(trafficMessage: TrafficMessage) {

        println("Received ${trafficMessage.badFlows.size} new bad flows.")
        println("Received ${trafficMessage.goodFlows.size} new good flows.")

        // insert unfiltered bad flows into trie
        ruleGenTree.insert(trafficMessage.badFlows, trafficMessage.goodFlows, time = trafficMessage.time)

        var reinit = ruleGenTree.getSelectedNodes().toList()

        val rules = ruleGenTree.generateRules(profile = ruleGenerationProfile, time = trafficMessage.time)

        val rulesPartition = rules.partition { rule -> !deployedRuleMap.containsKey(rule) }
        val toDeploy = rulesPartition.first
        val toKeep = rulesPartition.second
        val toRevokeRules = deployedRuleMap.keys.filter { !rules.contains(it) }
        val toRevokeIds = toRevokeRules.mapNotNull { deployedRuleMap[it] }

        if(toRevokeIds.isNotEmpty()) {
            println("To revoke: ${toRevokeIds.size} rules")
            print("\tSending to proxy @ ")
            val response = deleteRulesFromProxy(toRevokeIds)
            if (response == 200) {
                println("\tRevocation successful")
                toRevokeRules.forEach { deployedRuleMap.remove(it) }
            }
            else {
                println("\tRevocation failed")
            }
        }

        if (toDeploy.isNotEmpty()) {
            println("To deploy: ${toDeploy.size} rules")

            // Translate the rules to the proper format
            val msg = RuleUtils.translateSourceListToMsg(toDeploy)

            print("\tSending to proxy @ ")
            // Send rules to proxy controller and get response code
            val response = submitRulesToProxy(msg)

            if (response == 200) {
                println("\tDeployment successful")
                //Add new rules to hashset of deployed rules
                for (rule in msg.flowRules) {
                    deployedRuleMap.put(rule.flowFields["nw_src"]!!, rule.flowRuleId)
                }
            } else {
                println("\tDeployment failed")
            }
        }

        ruleGenTree.resetSelectedNodes(HashSet(reinit))

    }

    private fun submitRulesToProxy(msg: ClientCreateRuleMsg): Int {

        msg.clientId = "42"
        msg.clientDestination = "Test"

        val httpClient = ClientBuilder.newBuilder().register(JacksonFeature::class.java).build()
        val wt = httpClient.target("http://" + this.providerAddress).path("/Anon/client/deploy")

        println(wt.uri)

        val response = wt.request().post(Entity.json(msg))

        val responseString = response.readEntity(String::class.java)

        if (responseString != null) {
//            println("Response Status: " + response.status)
        }

        response.close()

        return response.status
    }

    private fun deleteRulesFromProxy(ids: List<String>): Int {
        val msg = ClientDeleteFlowRuleMsg()
        msg.clientId = "42"
        msg.flowRuleIds = ids

        val httpClient = ClientBuilder.newBuilder().register(JacksonFeature::class.java).build()
        val wt = httpClient.target("http://" + this.providerAddress).path("/Anon/client/delete")

        println(wt.uri)

        val response = wt.request().post(Entity.json(msg))

        val responseString = response.readEntity(String::class.java)

        if (responseString != null) {
//            println("Response Status: " + response.status)
        }

        response.close()

        return response.status
    }

    fun clearRules(): Int {

        val httpClient = ClientBuilder.newBuilder().build()
        val wt = httpClient.target("http://" + this.providerAddress).path("/Anon/client/clear")

        println(wt.uri)

        val response = wt.request().get()

        val responseString = response.readEntity(String::class.java)

        if (responseString != null) {
            println("Response Status: " + response.status)
        }

        response.close()

        return response.status
    }


    companion object {
        /**
         * create singleton instance
         */
        val instance = RuleGenerator()
    }
}

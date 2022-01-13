package Anon.client.service.utils;

import Anon.client.service.core.resource.FlowRule;
import Anon.client.service.utils.Random;
import Anon.client.service.message.client.ClientCreateRuleMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Rule related utility functions
 */
public class RuleUtils {

    /**
     * This function translates a list of source IP prefixes into a ClientCreateRuleMsg.
     *
     * @param rules a list of IP prefixes
     * @return a ClientCreateRuleMsg object
     */

    public static ClientCreateRuleMsg translateSourceListToMsg(List<String> rules) {

        ClientCreateRuleMsg msg = new ClientCreateRuleMsg();
        List<FlowRule> msgRules = new ArrayList<>();

        for(String rule : rules) {
            FlowRule flowRule = new FlowRule();
            HashMap<String, String> map = new HashMap<>();
            map.put("nw_src", rule);
            flowRule.setFlowFields(map);
            flowRule.setFlowRuleId(Random.randomString(8));
            msgRules.add(flowRule);
        }
        msg.setFlowRules(msgRules);

        return msg;
    }


}

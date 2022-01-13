package Anon.controller.service.core.rule;

import Anon.controller.service.message.client.ClientDeployMsg;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Anon and Openflow rule related utility functions
 */
class RuleUtils {

    /**
     * This function translate a ClientDeployMsg into a list of OpenFlow rules that implement the request.
     *
     * @param msg a object of ClientDeployMsg
     * @return a list of OpenflorRule objects
     */
    public static List<OpenflowRule> translateMsgToRules(ClientDeployMsg msg) {

        List<OpenflowRule> ofRules = new ArrayList<>();

        // set variable from customer's request
        String nw_dst = msg.getClientDestination();
        List<FlowRule> customerRules = msg.getFlowRules();

        for (FlowRule customerRule : customerRules) {
            /*
              will see if we need to check the obj size.
            if(rulePair.size()!=2){
                System.err.println("Rule IP pair size not 2!");
                rules.clear();
                return rules;
            }
             */
            HashMap<String, String> ruleFields = customerRule.getFlowFields();

            InetAddressValidator validator = new InetAddressValidator();

            // validate input IP addresses.
            if (!validator.isValidInet4Address(ruleFields.get("nw_src"))
                    || !validator.isValidInet4Address(nw_dst)) {
                ofRules.clear();
                return ofRules;
            }

            /*
             * need more condition checks to make sure users' requests are clear.
             */
            OpenflowRule ofRule = new OpenflowRule();

            // set src and dst IP (prefix)
            ofRule.setNw_src(ruleFields.get("nw_src"));
            ofRule.setNw_dst(nw_dst);
            ofRule.setActions(customerRule.getFlowAction());
            // check if user has defined network protocol
            if (ruleFields.containsKey("nw_proto")) {
                ofRule.setNw_proto(ruleFields.get("nw_proto"));
                if (ruleFields.containsKey("tp_src"))
                    ofRule.setTp_src(ruleFields.get("tp_src"));
                if (ruleFields.containsKey("tp_dst"))
                    ofRule.setTp_dst(ruleFields.get("tp_dst"));
            }
            ofRules.add(ofRule);
        }

        return ofRules;
    }
}

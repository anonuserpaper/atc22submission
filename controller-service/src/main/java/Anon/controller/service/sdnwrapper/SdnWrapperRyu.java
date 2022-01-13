package Anon.controller.service.sdnwrapper;

import com.fasterxml.jackson.databind.JsonNode;
import Anon.controller.service.core.rule.OpenflowRule;
import Anon.controller.service.message.sdn.ryu.RyuCreateRuleMsg;
import Anon.controller.service.message.sdn.ryu.RyuDeleteRuleMsg;
import Anon.controller.service.message.sdn.ryu.RyuMsgUtils;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * The implementation of SdnWrapper for Ryu SDN controller.
 */
public class SdnWrapperRyu extends SdnWrapper {
    public SdnWrapperRyu(String baseURI) {
        super(baseURI);
    }

    /**
     * Install a OpenFlow rule on a SDN switch
     *
     * @param rules      the openflow rules to be installed
     * @param sdnSwitch the SDN switch for rule installation
     * @return rule id in string format
     */
    @Override
    public String deployRules(List<OpenflowRule> rules, SdnSwitch sdnSwitch) {

        String apiUrl = "/firewall/rules-batch/" + sdnSwitch.getSdnSwitchId();

        WebTarget wt = httpClient.target(baseURI).path(apiUrl);

        /*
        OpenflowRule r = new OpenflowRule();
        r.setNw_src("10.0.0.1");
        r.setNw_dst("10.0.0.2");
        r.setActions("DENY");
        r.setPriority("10");
        */
        ArrayList<RyuCreateRuleMsg> msgs = new ArrayList<>();
        for (OpenflowRule r : rules) {
            RyuCreateRuleMsg msg = RyuMsgUtils.translateCreateRuleMsg(r);
            msgs.add(msg);
        }

        Response response = wt.request().post(Entity.json(msgs));

        String responseString = response.readEntity(String.class);
        JsonNode n = jsonStringToJsonNode(responseString);
        if (n != null)
            System.out.println(n.toString());

        response.close();

        List<String> ruleids = extractCreateRuleIds(n);
        for (int i = 0; i < rules.size(); i++) {
            rules.get(i).setRule_id(ruleids.get(i));
        }

        return "success";
    }

    /**
     * Delete a OpenFlow rule from a SDN switch
     *
     * @param rule_ids   the ID of the rule to be deleted
     * @param sdnSwitch the SDN switch object
     * @return rule deletion status
     */
    @Override
    public String deleteRules(List<String> rule_ids, SdnSwitch sdnSwitch) {

        /*
        curl -X DELETE -d '{"rule_id": "5"}' http://localhost:8080/firewall/rules/0000000000000001
         */
        // System.out.println(String.format("Delete rule %s, at switch %s", rule_id, sdnSwitch.getSdnSwitchId()));

        String apiUrl = "/firewall/rules/delete/" + sdnSwitch.getSdnSwitchId();

        WebTarget wt = httpClient.target(baseURI).path(apiUrl);

        //Response response = wt.request().post(Entity.json(rule));
        for (String rule_id : rule_ids) {
            RyuDeleteRuleMsg msg = new RyuDeleteRuleMsg(rule_id);
            Response response = wt.request().post(Entity.json(msg));

            String responseString = response.readEntity(String.class);
            try {
                JsonNode n = jsonStringToJsonNode(responseString);
                if (n != null)
                    System.out.println(n.toString());
            } catch (Exception e) {
                System.out.println(String.format("Delete ERROR: %s", responseString));
            }

            response.close();
        }

        //return extractDeleteRuleResponse(n);
        return "";
    }


    /**
     * Private function that translate a Ryu return message for rule creation attempt into a String format
     *
     * @param node the JsonNode object of the Ryu return message
     * @return the string format of the rule creation message
     */
    private List<String> extractCreateRuleIds(JsonNode node) {

        ArrayList<String> ruleIds = new ArrayList<>();

        for (JsonNode m : node.get(0).get("command_result")) {
            if (m.get("result").textValue().equals("success")) {
                ruleIds.add(m.get("rule_id").textValue());
            }
        }

        return ruleIds;
    }

}

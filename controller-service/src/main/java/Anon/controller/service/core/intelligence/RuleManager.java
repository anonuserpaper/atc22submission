package Anon.controller.service.core.intelligence;

import Anon.controller.service.core.authentication.ControllerIdentity;
import Anon.controller.service.core.rule.FlowRule;
import Anon.controller.service.message.client.ClientDeleteMsg;
import Anon.controller.service.message.client.ClientDeployMsg;
import Anon.controller.service.sdnwrapper.SdnWrapper;
import Anon.controller.service.sdnwrapper.SdnWrapperRyu;
import Anon.controller.service.webserver.ServerConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * The RuleManager class maintains all the Anon rules created by the clients.
 * The operations include: create rule; deploy rule; delete rule.
 */
public class RuleManager {

    /**
     * create singleton instance
     */
    private static RuleManager instance = new RuleManager();

    /**
     * make a hash map object of client ids, and each value of the map is a list of rules,
     * for indexing purpose.
     */
    private HashMap<String, HashMap<String, FlowRule>> clientRuleMap;

    /**
     * The SDN wrapper object that communicates with the SDN controller.
     */
    private SdnWrapper sdnWrapper;

    /**
     * RuleDeploymentRemote class handles placing rules in remote locations
     */
    private RuleDeploymentRemote remoteDeployment;

    /**
     * RuleDeploymentLocal class handles placing rules in local locations
     */
    private RuleDeploymentLocal localDeployment;

    /**
     * private constructor
     * make sure only one instance wil be created
     */
    private RuleManager() {
        clientRuleMap = new HashMap<>();
        sdnWrapper = new SdnWrapperRyu(ServerConfig.RyuControllerBaseURL);
        // sdnWrapper = new SdnWrapperSimulator(ServerConfig.TrafficSimulatorBaseURL);

        remoteDeployment = new RuleDeploymentRemote();
        localDeployment = new RuleDeploymentLocal();
    }

    public static RuleManager getInstance() {
        return instance;
    }

    /**
     * Create new Anon rule according to the client's requests.
     *
     * @param msg the rule creation message from the client
     */
    public void deployRules(ClientDeployMsg msg) {
        String clientId = msg.getClientId();
        if (clientId == null || clientId.equals("")) {
            clientId = "default";
        }
        if (!clientRuleMap.containsKey(clientId)) {
            clientRuleMap.put(clientId, new HashMap<>());
        }
        for (FlowRule rule : msg.getFlowRules()) {
            clientRuleMap.get(clientId).put(rule.getFlowRuleId(), rule);
        }

        HashMap<FlowRule, List<Integer>> ruleDeploymentMap = remoteDeployment.setDeploymentLocations(msg.getFlowRules());
        // TODO: store deployment location

        for (Map.Entry<FlowRule, List<Integer>> entry : ruleDeploymentMap.entrySet()) {
            FlowRule rule = entry.getKey();
            List<Integer> asns = entry.getValue();

            for (Integer asn : asns) {
                if (asn == ControllerIdentity.Companion.getAsNumber()) {
                    // local deployment
                    // TODO: deploy locally: contacting local SDN controller
                } else {
                    // remote deployment
                    // TODO: deploy remotely: contacting remote AS
                }
            }
        }
    }

    public void deleteRules(ClientDeleteMsg msg) {

        String clientId = msg.getClientId();
        for (String ruleid : msg.getFlowRuleIds()) {
            clientRuleMap.get(clientId).remove(ruleid);
        }

        // TODO: handle remote deletion
        sdnWrapper.deleteRules(msg.getFlowRuleIds(), null);
    }
}

package Anon.client.service.message.client;

import java.util.List;

/**
 * <p>
 * The rule deletion message from a client
 */
public class ClientDeleteFlowRuleMsg extends ClientMsg {

    /**
     * The ID of the rule deletion requesting client
     */
    private String clientId;

    /**
     * The ID of the Anon rule that is requested to be deleted
     */
    private List<String> flowRuleIds;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getFlowRuleIds() {
        return flowRuleIds;
    }

    public void setFlowRuleIds(List<String> flowRuleIds) {
        this.flowRuleIds = flowRuleIds;
    }

}

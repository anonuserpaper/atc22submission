package Anon.client.service.message.client;

/**
 * <p>
 * The rule deletion message from a client
 */
public class ClientDeleteRuleMsg extends ClientMsg {

    /**
     * The ID of the rule deletion requesting client
     */
    private String clientId;

    /**
     * The ID of the Anon rule that is requested to be deleted
     */
    private String AnonRuleId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAnonRuleId() {
        return AnonRuleId;
    }

    public void setAnonRuleId(String AnonRuleId) {
        this.AnonRuleId = AnonRuleId;
    }
}

package Anon.controller.service.message.remote;


import java.util.List;

/**
 * <p>
 * The rule deletion message from a client
 */
public class DeleteRuleMsg {

    private List<String> flowRuleIds;

    public List<String> getFlowRuleIds() {
        return flowRuleIds;
    }

    public void setFlowRuleIds(List<String> flowRuleIds) {
        this.flowRuleIds = flowRuleIds;
    }

}

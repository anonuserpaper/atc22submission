package Anon.controller.service.core.rule;

import java.util.HashMap;

/**
 * FlowInfo-level rules. One level more specific than Anon rules.
 * Not dependent on the OpenFlow and any dataplane protocol.
 * <p>
 */
public class FlowRule {

    /**
     * ID of the specific flow rule
     */
    private String flowRuleId;

    /**
     * Actions for matching packets:
     * - DENY
     * - ALLOW
     * - FORWARD
     */
    private String flowAction;
    /**
     * Field values for the flow description.
     * e.g. nw_src, nw_dst, nw_proto
     */
    private HashMap<String, String> flowFields;

    public String getFlowRuleId() {
        return flowRuleId;
    }

    public void setFlowRuleId(String flowRuleId) {
        this.flowRuleId = flowRuleId;
    }

    public String getFlowAction() {
        return flowAction;
    }

    public void setFlowAction(String flowAction) {
        this.flowAction = flowAction;
    }

    public HashMap<String, String> getFlowFields() {
        return flowFields;
    }

    public void setFlowFields(HashMap<String, String> flowFields) {
        this.flowFields = flowFields;
    }
}

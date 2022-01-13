package Anon.controller.service.message.client;

import Anon.controller.service.core.rule.FlowRule;

import java.util.List;

/**
 * <p>
 * The rule creation message.
 * All the fields should be filled automatically by the web-servier's JSON object manager.
 * The JSON object sent from the client will be translated into field values and put here.
 * <p>
 * Note: there is no support for IPv6 yet
 */
public class ClientDeployMsg extends ClientMsg {
    /**
     * Unique ID of the Anon client.
     */
    private String clientId;

    /**
     * The matching destination IP.
     * The destination IP should belongs to the client, otherwise Anon will discard the requests
     */
    private String clientDestination;

    /**
     * The matching fields of the flow matching rule.
     * e.g. each  rule object (hashmap) can be:
     * nw_proto: TCP, UDP, ICMP
     * nw_src: 1.1.1/24
     * tp_src: 123
     * tp_dst: 80
     */
    private List<FlowRule> flowRules;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientDestination() {
        return clientDestination;
    }

    public void setClientDestination(String clientDestination) {
        this.clientDestination = clientDestination;
    }

    public List<FlowRule> getFlowRules() {
        return flowRules;
    }

    public void setFlowRules(List<FlowRule> flowRules) {
        this.flowRules = flowRules;
    }
}

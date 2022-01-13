package Anon.controller.service.message.admin;

import Anon.controller.service.core.rule.FlowRule;

import java.util.HashMap;

/**
 * Message for deployment visualization.
 * <p>
 */
class DeploymentVisMsg {
    private HashMap<Integer, FlowRule> deployment;

    public HashMap<Integer, FlowRule> getDeployment() {
        return deployment;
    }

    public void setDeployment(HashMap<Integer, FlowRule> deployment) {
        this.deployment = deployment;
    }
}

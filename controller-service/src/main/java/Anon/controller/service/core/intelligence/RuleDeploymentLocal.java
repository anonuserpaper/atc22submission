package Anon.controller.service.core.intelligence;

import Anon.controller.service.core.rule.FlowRule;
import Anon.controller.service.message.client.ClientDeployMsg;
import Anon.controller.service.sdnwrapper.SdnSwitch;

import java.util.List;

/**
 * Local rule deployment algorithm to determine the set of SDN switches to deploy input rules.
 * <p>
 */
class RuleDeploymentLocal {

    private ResourceManager resourceManager;

    public RuleDeploymentLocal() {
        this.resourceManager = ResourceManager.getInstance();
    }

    /**
     * Set the the desired deployment location of a Anon rule to all its generated OpenFlow rules.
     *
     * @param rule the Anon rule to be deployed
     * @param msg  the client request message that may contains the customized deployment location.
     */
    private void setDeploymentLocations(List<FlowRule> rules, ClientDeployMsg msg) {

        // find a switch to deploy
        //String switchId = msg.getTmpSwitchId();
        String switchId = "";
        SdnSwitch sdnSwitch;

        //System.out.println("DEBUG: EMPTY SWITCHID, USE DEFAULT");
        sdnSwitch = resourceManager.getSwitch();

        if (sdnSwitch == null) {
            System.out.println(String.format("DEBUG: FAILED TO FIND SWITCHID %s", switchId));
            // if we failed to find a corresponding switch, the program will rage quit.
            return;
        } else {
            System.out.println(String.format("DEBUG: FOUND SWITCHID %s", switchId));
        }

        // TODO: translate FlowRule to OpenFlowRule
        // for (OpenflowRule r : rule.getOpenflowRules()) {
        //     r.setDeployedSdnSwitch(sdnSwitch);
        // }
    }
}

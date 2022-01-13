package Anon.controller.service.core.rule;

import Anon.controller.service.sdnwrapper.SdnSwitch;

/**
 * <p>
 * The OpenflowRule class defines the OpenFlow rules pertinent to Anon project
 */

public class OpenflowRule {

    /**
     * a unique id for each rule
     */
    private String rule_id;
    /**
     * the switch deployed this rule
     */
    private SdnSwitch deployedSdnSwitch;
    /**
     * priority of the rule
     */
    private String priority = "10";
    /**
     * network layer source and destination address
     */
    private String nw_src, nw_dst;
    /**
     * the action on the matched traffic. ALLOW or DENY
     */
    private String actions = "DENY";

    /**
     * network layer protocol TCP or UDP
     */
    private String nw_proto;

    /**
     * transport layer source and destination port
     */
    private String tp_src;
    private String tp_dst;

    public SdnSwitch getDeployedSdnSwitch() {
        return deployedSdnSwitch;
    }

    public void setDeployedSdnSwitch(SdnSwitch deployedSdnSwitch) {
        this.deployedSdnSwitch = deployedSdnSwitch;
    }

    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public String getTp_dst() {
        return tp_dst;
    }

    public void setTp_dst(String tp_dst) {
        this.tp_dst = tp_dst;
    }

    public String getTp_src() {
        return tp_src;
    }

    public void setTp_src(String tp_src) {
        this.tp_src = tp_src;
    }

    public String getNw_proto() {
        return nw_proto;
    }

    public void setNw_proto(String nw_proto) {
        this.nw_proto = nw_proto;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNw_src() {
        return nw_src;
    }

    public void setNw_src(String nw_src) {
        this.nw_src = nw_src;
    }

    public String getNw_dst() {
        return nw_dst;
    }

    public void setNw_dst(String nw_dst) {
        this.nw_dst = nw_dst;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }
}

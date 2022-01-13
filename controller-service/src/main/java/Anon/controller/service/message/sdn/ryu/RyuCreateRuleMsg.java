package Anon.controller.service.message.sdn.ryu;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * The class that defines the rule creation message for Ryu controller
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RyuCreateRuleMsg {

    /*
     Fields needed:
     #     <field>  : <value>
     #    "priority": "0 to 65533"
     #    "dl_type" : "<ARP or IPv4>"
     #    "nw_src"  : "<A.B.C.D/M>" if ip is complete no need for /M
     #    "nw_dst"  : "<A.B.C.D/M>" if ip is complete no need for /M
     #    "nw_proto": "<TCP or UDP or ICMP>"
     #    "tp_src"  : "<int>" process port
     #    "tp_dst"  : "<int>" process port
     #    "actions" : "<ALLOW or DENY>"
     */

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

    // source and destination port number
    /**
     * transport layer source and destination port
     */
    private String tp_src, tp_dst;

    /**
     * dl_type ?
     */
    //private String dl_type;
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

    public String getNw_proto() {
        return nw_proto;
    }

    public void setNw_proto(String nw_proto) {
        this.nw_proto = nw_proto;
    }

    public String getTp_src() {
        return tp_src;
    }

    public void setTp_src(String tp_src) {
        this.tp_src = tp_src;
    }

    public String getTp_dst() {
        return tp_dst;
    }

    public void setTp_dst(String tp_dst) {
        this.tp_dst = tp_dst;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

}


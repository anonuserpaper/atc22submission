package Anon.controller.service.message.sdn;


/**
 * <p>
 * The messages that tells Anon there is a switch leaving the network
 */
public class SdnSwitchLeaveMsg extends SdnMsg {

    /**
     * The ID of the switch that left the network.
     */
    private String sdnSwitchId;

    public String getSdnSwitchId() {
        return sdnSwitchId;
    }

    public void setSdnSwitchId(String sdnSwitchId) {
        this.sdnSwitchId = sdnSwitchId;
    }
}

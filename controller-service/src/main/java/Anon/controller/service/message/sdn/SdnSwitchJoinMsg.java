package Anon.controller.service.message.sdn;

/**
 * <p>
 * Message from the SDN controller telling Anon that there is a new switch joining the network.
 */
public class SdnSwitchJoinMsg extends SdnMsg {

    /**
     * The ID of the joined Switch
     */
    private String sdnSwitchId;

    public String getSdnSwitchId() {
        return sdnSwitchId;
    }

    public void setSdnSwitchId(String sdnSwitchId) {
        this.sdnSwitchId = sdnSwitchId;
    }
}

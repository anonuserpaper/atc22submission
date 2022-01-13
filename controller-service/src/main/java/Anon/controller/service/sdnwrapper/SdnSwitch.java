package Anon.controller.service.sdnwrapper;

/**
 * the instance of this class contains information about a switch
 * <p>
 * Currently, there is only one field of information about any switch which is the ID of a switch.
 * We will add more information as needed in the future versions of Anon.
 */
public class SdnSwitch {

    /**
     * The ID of this switch.
     */
    private String sdnSwitchId;

    /**
     * Public constructor function
     *
     * @param id
     */
    public SdnSwitch(String id) {
        this.sdnSwitchId = id;
        System.out.println("switch object created! " + id);
    }

    /**
     * Get the ID of this switch
     *
     * @return the ID of the switch in String format
     */
    public String getSdnSwitchId() {
        return sdnSwitchId;
    }
}

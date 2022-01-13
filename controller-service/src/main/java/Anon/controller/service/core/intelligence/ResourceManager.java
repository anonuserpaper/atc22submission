package Anon.controller.service.core.intelligence;

import Anon.controller.service.sdnwrapper.SdnSwitch;

import java.util.HashMap;

/**
 * <p>
 * The ResourceManager class mainly handles the switches in any SDN network.
 * The SDN switches are connected to the SDN controller, and the ResourceManager maintains a list of the connected
 * switches in the switchMap hash map.
 */

public class ResourceManager {

    private static ResourceManager instance = new ResourceManager();
    /**
     * The main hash map that maintains all the switches connected to the SDN controller.
     * The key is the switch ID and the values is the corresponding SdnSwitch object
     *
     * @see SdnSwitch
     */
    private HashMap<String, SdnSwitch> switchMap;

    private ResourceManager() {
        switchMap = new HashMap<>();
    }

    public static ResourceManager getInstance() {
        return instance;
    }

    /**
     * Get an SdnSwitch object given a switchId
     *
     * @param switchId the id string of the requested switch
     * @return a corresponding SdnSwitch object or null
     */
    public SdnSwitch getSwitch(String switchId) {
        SdnSwitch res;
        res = switchMap.getOrDefault(switchId, null);

        return res;
    }

    /**
     * Get the first SdnSwitch object in the switchMap hash map.
     *
     * @return the first SdnSwitch object in the hash map
     */
    public SdnSwitch getSwitch() {
        return switchMap.entrySet().iterator().next().getValue();
    }

    /**
     * Add a new SdnSwitch object into the hash map.
     *
     * @param switchId the id of the to-add switch
     */
    public void addSwitch(String switchId) {
        if (!switchMap.containsKey(switchId)) {
            switchMap.put(switchId, new SdnSwitch(switchId));
        }
    }

    /**
     * Delete an existing SdnSwitch object in the switchMap hash map
     *
     * @param switchId the id string of the to-delete switch
     */
    public void deleteSwitch(String switchId) {
        switchMap.remove(switchId);
    }

}

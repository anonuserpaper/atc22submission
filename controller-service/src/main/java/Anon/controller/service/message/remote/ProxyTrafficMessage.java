package Anon.controller.service.message.remote;

import java.util.HashMap;
import java.util.List;

/**
 * Traffic information send to proxy controller.
 * The message contains all flows, as well as filter information.
 */
public class ProxyTrafficMessage {

    private String time;
    private List<FlowInfo> flows;
    private HashMap<String, HashMap<String, Integer>> filterCount;

    public HashMap<String, HashMap<String, Integer>> getFilterCount() {
        return filterCount;
    }

    public void setFilterCount(HashMap<String, HashMap<String, Integer>> filterCount) {
        this.filterCount = filterCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<FlowInfo> getFlows() {
        return flows;
    }

    public void setFlows(List<FlowInfo> flows) {
        this.flows = flows;
    }
}

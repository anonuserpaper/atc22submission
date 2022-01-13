package Anon.client.service.core.resource;

import Anon.client.rulegeneration.core.resource.Flow;

import java.util.List;

/**
 * Traffic information send to proxy controller.
 * The message contains all flows, as well as filter information.
 */
public class TrafficMessage {

    private int time;
    private List<Flow> badFlows;

    private List<Flow> goodFlows;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<Flow> getBadFlows() {
        return badFlows;
    }

    public void setBadFlows(List<Flow> badFlows) {
        this.badFlows = badFlows;
    }

    public List<Flow> getGoodFlows() {
        return goodFlows;
    }

    public void setGoodFlows(List<Flow> goodFlows) {
        this.goodFlows = goodFlows;
    }
}

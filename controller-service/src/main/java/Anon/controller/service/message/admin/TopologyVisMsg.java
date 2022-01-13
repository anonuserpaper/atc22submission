package Anon.controller.service.message.admin;

import java.util.List;
import java.util.Set;

/**
 * Message for topology visualization
 * The message provide two information:
 * - list of defender nodes
 * - list of attack paths
 * <p>
 */
class TopologyVisMsg {
    private String timestr;
    private Set<Integer> defenders;
    private Set<List<Integer>> goodSeg;
    private Set<List<Integer>> badSeg;

    public String getTimestr() {
        return timestr;
    }

    public void setTimestr(String timestr) {
        this.timestr = timestr;
    }

    public Set<Integer> getDefenders() {
        return defenders;
    }

    public void setDefenders(Set<Integer> defenders) {
        this.defenders = defenders;
    }

    public Set<List<Integer>> getGoodSeg() {
        return goodSeg;
    }

    public void setGoodSeg(Set<List<Integer>> goodSeg) {
        this.goodSeg = goodSeg;
    }

    public Set<List<Integer>> getBadSeg() {
        return badSeg;
    }

    public void setBadSeg(Set<List<Integer>> badSeg) {
        this.badSeg = badSeg;
    }
}

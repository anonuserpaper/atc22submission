package Anon.controller.service.message.sdn.ryu;

/**
 * <p>
 * The class defines the rule deletion message for Ryu controller
 */
public class RyuDeleteRuleMsg {

    private String rule_id;

    /**
     * Constructor function.
     *
     * @param ruleid the id of the rule to be deleted
     */
    public RyuDeleteRuleMsg(String ruleid) {
        this.rule_id = ruleid;
    }

    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }
}

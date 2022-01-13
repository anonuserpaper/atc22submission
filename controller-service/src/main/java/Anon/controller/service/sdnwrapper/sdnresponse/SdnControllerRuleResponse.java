package Anon.controller.service.sdnwrapper.sdnresponse;


/**
 */

class SdnControllerRuleResponse {
    // if we have message object names that are different from the json messages sdn controller api provides
    // use this.
    //@JsonProperty("owner")
    //public void setO(Object o) {

    private boolean status;
    private int ruleId;

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


}

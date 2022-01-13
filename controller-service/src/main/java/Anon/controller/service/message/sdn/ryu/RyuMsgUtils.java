package Anon.controller.service.message.sdn.ryu;

import Anon.controller.service.core.rule.OpenflowRule;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * Utility functions for Ryu controller message related tasks
 */
public class RyuMsgUtils {

    /**
     * Translate a OpenFlowRule into a Ryu controller message.
     *
     * @param rule openflow-rule to be translated
     * @return a Ryu rule-creation message
     */
    public static RyuCreateRuleMsg translateCreateRuleMsg(OpenflowRule rule) {
        RyuCreateRuleMsg msg = new RyuCreateRuleMsg();

        msg.setActions(rule.getActions());
        msg.setNw_dst(rule.getNw_dst());
        msg.setNw_src(rule.getNw_src());
        msg.setPriority(rule.getPriority());

        // check if request filled network protocol
        if (!StringUtils.isEmpty(rule.getNw_proto())) {
            msg.setNw_proto(rule.getNw_proto());

            // check if request filled transport layer port
            if (!StringUtils.isEmpty(rule.getTp_dst())) {
                msg.setTp_dst(rule.getTp_dst());
            }
            if (!StringUtils.isEmpty(rule.getTp_src())) {
                msg.setTp_src(rule.getTp_src());
            }
        }

        return msg;
    }
}

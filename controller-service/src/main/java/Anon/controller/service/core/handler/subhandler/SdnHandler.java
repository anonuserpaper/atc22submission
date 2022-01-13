package Anon.controller.service.core.handler.subhandler;

import Anon.controller.service.core.handler.MessageHandlerInterface;
import Anon.controller.service.core.intelligence.ResourceManager;
import Anon.controller.service.message.AnonMessage;
import Anon.controller.service.message.sdn.SdnSwitchJoinMsg;
import Anon.controller.service.message.sdn.SdnSwitchLeaveMsg;

import java.util.HashMap;

/**
 */
public class SdnHandler implements MessageHandlerInterface {


    private ResourceManager resourceManager;

    public SdnHandler() {
        this.resourceManager = ResourceManager.getInstance();
    }


    @Override
    public HashMap<String,String> handleMessage(AnonMessage msg) {
        if (msg instanceof SdnSwitchJoinMsg) {
            switchJoin((SdnSwitchJoinMsg) msg);
        } else if (msg instanceof SdnSwitchLeaveMsg) {
            switchLeave((SdnSwitchLeaveMsg) msg);
        }
        return null;
    }


    private void switchJoin(SdnSwitchJoinMsg msg) {
        resourceManager.addSwitch(msg.getSdnSwitchId());
    }

    private void switchLeave(SdnSwitchLeaveMsg msg) {
        resourceManager.deleteSwitch(msg.getSdnSwitchId());
    }

}

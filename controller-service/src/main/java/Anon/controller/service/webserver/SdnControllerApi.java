package Anon.controller.service.webserver;

import Anon.controller.service.core.handler.MainHandler;
import Anon.controller.service.message.sdn.SdnSwitchJoinMsg;
import Anon.controller.service.message.sdn.SdnSwitchLeaveMsg;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * <p>
 * The REST API handler for calls from SDN controllers
 * The calls are meant for SDN controllers to actively update the network status
 */

@Path("sdn")
public class SdnControllerApi {

    @Inject
    private
    MainHandler mainHandler;

    /**
     * The function that receives the switchJoin messages from a SDN controller
     *
     * @param msg the SdnSwitchJoinMsg object
     * @return the status message from Anon
     */
    @Path("switch_join/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String switchJoin(SdnSwitchJoinMsg msg) {
        mainHandler.handleMessage(msg);
        System.out.println("switch joined!" + msg.getSdnSwitchId());

        return "switch joined";
    }

    /**
     * The function that receives the switchLeave messages from a SDN controller
     *
     * @param msg the SdnSwitchLeaveMsg object
     * @return the status message from Anon
     */
    @Path("switch_leave/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String switchLeave(SdnSwitchLeaveMsg msg) {
        mainHandler.handleMessage(msg);
        System.out.println("switch left!" + msg.getSdnSwitchId());

        return "switch left";
    }
}

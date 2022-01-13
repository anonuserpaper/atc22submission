package Anon.controller.service.webserver;

import Anon.controller.service.core.handler.MainHandler;
import Anon.controller.service.message.client.ClientDeleteMsg;
import Anon.controller.service.message.client.ClientDeployMsg;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * ClientAPI is the entry class for the for Anon interfaces.
 * It takes the translated request messages and pass it to the Anon core logic.
 * <p>
 * NOTE: add authentication filters here.
 *
 * @see MainHandler
 */
@Path("client")
public class ClientAPI {
    // handle all client requests here in this class
    // the file may be really big depends on how many cases that a client will have.
    // but let's hope not. O.O

    @Inject
    private
    MainHandler mainHandler;

    // TODO: add login or register function to allow controller maintain some information about the client connected.

    /**
     * Takes an create rule message and calls the Anon core logic.
     *
     * @param msg the create rule message translated from the JSON object in the post request.
     * @return returns the rule creation results
     * @see ClientDeployMsg
     * @see Anon.controller.service.core.intelligence.AnonIntelligence#deployRules(ClientDeployMsg)
     */
    @Path("deploy/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap deployRule(ClientDeployMsg msg) {
        HashMap result = mainHandler.handleMessage(msg);
        return result;
    }

    /**
     * Delete a batch of open-flow rules deployed
     * @param msg delete message
     * @return return deleted rules
     */
    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap deleteRules(ClientDeleteMsg msg) {
        return mainHandler.handleMessage(msg);
    }
}

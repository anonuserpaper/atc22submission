package Anon.client.service.webserver;

import Anon.client.service.core.handler.MainHandler;
import Anon.client.service.message.client.ClientCreateRuleMsg;
import Anon.client.service.message.client.ClientDeleteRuleMsg;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * ClientAPI is the entry class for Anon interfaces.
 * It takes the translated request messages from the client front-end and passes them to the Anon core logic.
 *
 * NOTE: add authentication filters here.
 *
 * TODO: Make sure all logic is still appropriate for client-side front-end to back-end communication
 * TODO: Add new endpoints if necessary
 *
 * @see MainHandler
 */
@Path("client")
public class ClientAPI {

    @Inject
    MainHandler mainHandler;

    /**
     * Takes an create rule message and calls the Anon core logic.
     *
     * @param msg the create rule message translated from the JSON object in the post request.
     * @return returns the rule creation results
     * @see ClientCreateRuleMsg
     */
    @Path("create/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap createRule(ClientCreateRuleMsg msg) {
        return mainHandler.handleMessage(msg);
    }

    @Path("clear/")
    @GET
    public void clearRules() {
        mainHandler.ruleGenerator.clearRules();
    }


    @Path("delete/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap deleteRule(ClientDeleteRuleMsg msg){
        return mainHandler.handleMessage(msg);

    }



}

package Anon.controller.service.core.handler;

import Anon.controller.service.core.handler.subhandler.AdminHandler;
import Anon.controller.service.core.handler.subhandler.ClientHandler;
import Anon.controller.service.core.handler.subhandler.RemoteControllerHandler;
import Anon.controller.service.core.handler.subhandler.SdnHandler;
import Anon.controller.service.message.AnonMessage;
import Anon.controller.service.message.admin.AdminMessage;
import Anon.controller.service.message.client.ClientMsg;
import Anon.controller.service.message.remote.ControllerMsg;
import Anon.controller.service.message.sdn.SdnMsg;

import java.util.HashMap;

/**
 * <p>
 * This class define the entrace interfaces of different types of messages from different entities.
 * The object of this class is exposed to the networking component of Anon.
 */
public class MainHandler {

    private static int proxyAsn;
    /**
     * The handler class that handles the messages from Anon clients
     */
    private ClientHandler clientHandler;
    /**
     * The handler class that handles the messages from other Anon controllers
     */
    private RemoteControllerHandler remoteControllerHandler;
    /**
     * The handler class that handles the messages from local Anon operators
     */
    private AdminHandler adminHandler;
    /**
     * The handler class that communicates with the SDN controllers
     */
    private SdnHandler sdnHandler;
    /**
     * The Anon intelligence module that contains the core logic of Anon.
     */

    /**
     * The public contructor function that initiate objects of message handlers and Anon intelligence.
     */
    public MainHandler(int proxyAsn) {
        MainHandler.proxyAsn = proxyAsn;

        // initiate message handlers
        clientHandler = new ClientHandler();
        remoteControllerHandler = new RemoteControllerHandler();
        adminHandler = new AdminHandler();
        sdnHandler = new SdnHandler();
    }

    /**
     * Generic message handling function.
     * Based the entities that sent the messsage (i.e. client, admin, or controller), the function will forward the message
     * to the corresponding specific message handlers.
     *
     * @param msg
     * @return
     */
    public HashMap
    handleMessage(AnonMessage msg) {
        HashMap<String, String> returnMsg = new HashMap<>();

        if (msg instanceof ClientMsg) {
            returnMsg = clientHandler.handleMessage(msg);
        } else if (msg instanceof ControllerMsg) {
            //returnMsg = remoteControllerHandler.handleMessage(msg);
        } else if (msg instanceof AdminMessage) {
            //returnMsg = adminHandler.handleMessage(msg);
        } else if (msg instanceof SdnMsg) {
            returnMsg = sdnHandler.handleMessage(msg);
        }

        return returnMsg;
    }

}

package Anon.controller.service.core.handler.subhandler;

import Anon.controller.service.core.handler.MessageHandlerInterface;
import Anon.controller.service.core.intelligence.RuleManager;
import Anon.controller.service.message.AnonMessage;
import Anon.controller.service.message.client.ClientDeleteMsg;
import Anon.controller.service.message.client.ClientDeployMsg;

import java.util.HashMap;

/**
 * <p>
 * NOTE: remember to add lock for multi-threading support.
 * This class handles the remote_Anon_controller client.
 */
public class ClientHandler implements MessageHandlerInterface {

    /**
     * The singleton instance of RuleManager object
     */
    private RuleManager ruleManager;

    /**
     * public constructor function: takes an object of Anon intelligence object to save as reference.
     */
    public ClientHandler() {
        this.ruleManager = RuleManager.getInstance();
    }

    /**
     * Handles different types of messages sent from the client.
     * The message types include:
     * 1. Create rule message;
     * 2. Delete rule message;
     * 3. List rules message.
     * <p>
     * The messages will be transformed according to their types, and handled by the Anon's intelligence module.
     *
     * @param msg Anon message
     * @return A HashMap return values from the corresponding function call.
     */
    @Override
    public HashMap<String,String> handleMessage(AnonMessage msg) {

        HashMap<String, String> ret = new HashMap<>();

        if (msg instanceof ClientDeployMsg) {
            ruleManager.deployRules((ClientDeployMsg) msg);
        } else if (msg instanceof ClientDeleteMsg) {
            ruleManager.deleteRules((ClientDeleteMsg) msg);
        }

        return ret;
    }

}

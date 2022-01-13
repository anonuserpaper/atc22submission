package Anon.controller.service.core.handler;

import Anon.controller.service.message.AnonMessage;

import java.util.HashMap;

/**
 * <p>
 * The interface for handling Anon messages,
 * enforcing all handlers to implement.
 */
public interface MessageHandlerInterface {

    HashMap<String,String> handleMessage(AnonMessage msg);
}

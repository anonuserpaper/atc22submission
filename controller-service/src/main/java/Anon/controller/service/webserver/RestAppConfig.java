package Anon.controller.service.webserver;

import Anon.controller.service.core.handler.MainHandler;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 */

/**
 * register our web services, as well as JacsonFeature.class (a json (de)serialization library)
 * we made the main logic as a singleton object thus we will not need to worry about re init the whole system.
 */
class RestAppConfig extends ResourceConfig {

    /**
     * Constructor: register the classes.
     */
    RestAppConfig() {
        super();
        this.packages("Anon.controller.service.webserver");
        this.register(JacksonFeature.class);

        this.register(new AbstractBinder() {
            @Override
            protected void configure() {
                // singleton instance
                bind(new MainHandler(174)).to(MainHandler.class);
            }
        });
    }
}
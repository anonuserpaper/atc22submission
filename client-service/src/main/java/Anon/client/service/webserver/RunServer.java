package Anon.client.service.webserver;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * This is THE entrance of the whole system.
 * We init the system first, and serve it via Grizzly httpserver
 */
public class RunServer {

    /**
     * The constructor function that starts the Anon controller service.
     *
     * @return
     */
    public static HttpServer startServer() {
        RestAppConfig rc = new RestAppConfig();
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(ServerConfig.AnonBaseURL), rc);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
        HttpServer server = startServer();
        // this http handler will search webapp folder under resources folder
        HttpHandler httpHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/webapp/");
        server.getServerConfiguration().addHttpHandler(httpHandler, "/webapp/");

        System.out.println(String.format("Jersey app started with WADL available at "
                + "%s/application.wadl\nHit enter to stop it...", ServerConfig.AnonBaseURL));
        System.in.read();
        server.shutdownNow();
    }
}

package de.reinhard.merlin.app.jetty;

import de.reinhard.merlin.app.Configuration;
import de.reinhard.merlin.app.rest.RestRegistry;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class JettyServer {
    private Logger log = LoggerFactory.getLogger(JettyServer.class);
    private Server server;
    private int port;

    public void start() {
        port = findFreePort();
        if (port == -1) {
            return;
        }
        log.info("Starting web server on port " + port);
        server = new Server(port);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{ "resources/html/configure.html" });
        resourceHandler.setResourceBase(".");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
        resourceHandler.setRedirectWelcome(false);
        resourceHandler.setResourceBase("./merlin-webapp/build");

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/rest/*");
        jerseyServlet.setInitOrder(1);
        log.info("Rest services: " + RestRegistry.getInstance().getServiceList());
        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages",
                "de.reinhard.merlin.app.rest");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, context });
        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception ex) {
            log.error("Can't start jetty: " + ex.getMessage(), ex);
        }
    }

    public void stop() {
        log.info("Stopping web server.");
        try {
            server.stop();
        } catch (Exception ex) {
            log.error("Can't stop web server: " + ex.getMessage(), ex);
        }
        server.destroy();
    }

    private int findFreePort() {
        int port = Configuration.getInstance().getPort();
        for (int i = port; i < 8999; i++) {
            try (ServerSocket socket = new ServerSocket(i)) {
                return i;
            } catch (IOException ex) {
                log.info("Port " + i + " already in use. Trying next port.");
                continue; // try next port
            }
        }
        log.error("No free port found! Giving up.");
        return -1;
    }

    public int getPort() {
        return port;
    }
}


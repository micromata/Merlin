package de.reinhard.merlin.app.jetty;

import de.reinhard.merlin.app.Configuration;
import de.reinhard.merlin.app.rest.RestRegistry;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
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
/*
        ServerConnector connector = new ServerConnector(server);
        connector.setHost("127.0.0.1");
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});
*/
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setRedirectWelcome(false);

        // Wrap the ResourceHandler in a ContextHandler for the "/" path
        Resource basePath;
        try {
            basePath = Resource.newResource("./merlin-webapp/build");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return;
        }
        ContextHandler rootContextHandler = new ContextHandler();
        rootContextHandler.setContextPath("/");
        rootContextHandler.setBaseResource(basePath);
        rootContextHandler.setHandler(resourceHandler);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
        errorHandler.addErrorPage(404, "/index.html");
        contextHandler.setErrorHandler(errorHandler);

        ServletHolder jerseyServlet = contextHandler.addServlet(ServletContainer.class, "/rest/*");
        jerseyServlet.setInitOrder(1);
        log.info("Rest services: " + RestRegistry.getInstance().getServiceList());
        // Tells the Jersey Servlxet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages",
                "de.reinhard.merlin.app.rest");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{rootContextHandler, contextHandler});
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


package de.reinhard.merlin.app.jetty;

import de.reinhard.merlin.app.ConfigurationHandler;
import de.reinhard.merlin.app.javafx.RunningMode;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.EnumSet;

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
        server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setHost("127.0.0.1");
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

        ServletContextHandler ctx =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        ctx.setContextPath("/");


        ServletHolder jerseyServlet = ctx.addServlet(ServletContainer.class, "/rest/*");
        jerseyServlet.setInitOrder(1);
        // Tells the Jersey Servlxet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages",
                "de.reinhard.merlin.app.rest");

        try {
            // Resolve file to directory
            ctx.setBaseResource(Resource.newResource("./merlin-webapp/build"));
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return;
        }
        ctx.setWelcomeFiles(new String[]{"index.html"});
        ctx.addServlet(DefaultServlet.class, "/");

        ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
        errorHandler.addErrorPage(404, "/");
        ctx.setErrorHandler(errorHandler);

        if (RunningMode.isDevelopmentMode()) {
            log.warn("*********************************");
            log.warn("***********            **********");
            log.warn("*********** ATTENTION! **********");
            log.warn("***********            **********");
            log.warn("*********** Running in **********");
            log.warn("*********** dev mode!  **********");
            log.warn("***********            **********");
            log.warn("*********************************");
            log.warn("Don't deliver this app in dev mode due to security reasons!");

            FilterHolder filterHolder = ctx.addFilter(CrossOriginFilter.class,"/*", EnumSet.of(DispatcherType.REQUEST));
            filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
            filterHolder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
            filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
            filterHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        }

        server.setHandler(ctx);

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
        int port = ConfigurationHandler.getInstance().getConfiguration().getPort();
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


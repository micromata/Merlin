package de.reinhard.merlin.app.jetty;

import de.reinhard.merlin.app.Configuration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
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
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server = new Server(port);
        server.setHandler(context);
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


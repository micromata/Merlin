package de.micromata.paypal;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JettyServer {
    private Logger log = LoggerFactory.getLogger(JettyServer.class);
    private Server server;
    private int port;

    public void start(PayPalConfig paypalConfig, int port) {
        log.info("Starting web server on port " + port);
        PaymentReceiveServlet.setConfig(paypalConfig);
        PaymentCancelServlet.setConfig(paypalConfig);
        PaymentTestServlet.setPaypalConfig(paypalConfig);
        server = new Server(port);

        ServletContextHandler ctx =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        ctx.setContextPath("/");

        String applicationHome = System.getProperty("applicationHome");
        Path path;
        if (Utils.isBlank(applicationHome)) {
            applicationHome = System.getProperty("user.dir");
            log.info("applicationHome is not given as JVM   parameter. Using current working dir (OK for start in IDE): " + applicationHome);
            path = Paths.get(applicationHome, "merlin-paypal-server", "web");
        } else {
            path = Paths.get(applicationHome, "web");
        }

        try {
            if (!Files.exists(path)) {
                log.error("********** Fatal: Can't find web files: " + path.toAbsolutePath());
            }
            URL url = path.toUri().toURL();
            ctx.setBaseResource(Resource.newResource(url));
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return;
        }
        ctx.setWelcomeFiles(new String[]{"index.html"});
        ctx.setInitParameter(DefaultServlet.CONTEXT_INIT + "cacheControl", "no-store,no-cache,must-revalidate");//"max-age=5,public");
        ctx.setInitParameter(DefaultServlet.CONTEXT_INIT + "useFileMappedBuffer", "false");
        ctx.addServlet(DefaultServlet.class, "/");
        ctx.addServlet(PaymentReceiveServlet.class, "/receivePayment");
        ctx.addServlet(PaymentCancelServlet.class, "/cancelPayment");
        ctx.addServlet(PaymentTestServlet.class, "/testPayment");

        server.setHandler(ctx);

        try {
            server.start();
            server.join();
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
        if (server != null) {
            server.destroy();
        }
    }
}


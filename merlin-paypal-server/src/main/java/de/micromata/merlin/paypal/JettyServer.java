package de.micromata.merlin.paypal;

import de.micromata.merlin.paypal.sdk.PaymentCancelServlet;
import de.micromata.merlin.paypal.sdk.PaymentReceiveServlet;
import de.micromata.merlin.paypal.sdk.PaymentTestServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyServer {
    private Logger log = LoggerFactory.getLogger(JettyServer.class);
    private Server server;
    private int port;

    public void start(PaypalConfig paypalConfig, int port) {
        log.info("Starting web server on port " + port);
        PaymentReceiveServlet.setAPIContext(paypalConfig.getApiContext());
        PaymentCancelServlet.setAPIContext(paypalConfig.getApiContext());
        PaymentTestServlet.setPaypalConfig(paypalConfig);
        server = new Server(port);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(PaymentReceiveServlet.class, "/receivePayment");
        handler.addServletWithMapping(PaymentCancelServlet.class, "/cancelPayment");
        handler.addServletWithMapping(PaymentTestServlet.class, "/testPayment");

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


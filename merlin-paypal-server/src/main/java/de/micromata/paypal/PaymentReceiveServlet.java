package de.micromata.paypal;

import de.micromata.paypal.data.PaymentExecuted;
import de.micromata.paypal.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The url of this servlet should be configured in {@link PayPalConfig#getReturnUrl()}.
 * This is an example servlet for dealing with successful payments called by PayPal after the user
 * finished the payment process by committing the payment on the PayPal site.
 * <br/>
 * This is the last step of a payment process.
 * <br/>
 * After executing this payment, we have the money!
 */
public class PaymentReceiveServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(PaymentReceiveServlet.class);

    private static PayPalConfig config;

    public static void setConfig(PayPalConfig config) {
        PaymentReceiveServlet.config = config;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        if (config == null) {
            log.error("Don't forget to set config.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String paymentId = req.getParameter("paymentId");
        String payerId = req.getParameter("PayerID");
        try {
            executePayment(req, resp, paymentId, payerId);
        } catch (PayPalRestException ex) {
            log.error("Error while executing payment: " + ex.getMessage(), ex);
        }
    }

    /**
     * Override this method for own code for executing payments.
     *
     * @param req
     * @param resp
     * @param paymentId
     * @param payerId
     */
    protected void executePayment(HttpServletRequest req, HttpServletResponse resp, String paymentId, String payerId) throws IOException, PayPalRestException {
        log.info("Payment received: paymentId=" + paymentId + ", PayerID=" + payerId);
        if (Utils.isBlank(paymentId)) {
            log.error("Can't execute payment, paymentId not given. Aborting payment...");
            redirectToErrorPage(resp);
            return;
        }
        if (Utils.isBlank(payerId)) {
            log.error("Can't execute payment, payerId not given. Aborting payment...");
            redirectToErrorPage(resp);
            return;
        }
        PaymentExecuted paymentExecuted = PayPalConnector.executePayment(config, paymentId, payerId);
        if (paymentExecuted != null) {
            log.info("Payment executed: " + JsonUtils.toJson(paymentExecuted));
            resp.setStatus(302);
            resp.setHeader("Location", "/paymentExecuted.html");
            return;
        }
        redirectToErrorPage(resp);
    }

    private void redirectToErrorPage(HttpServletResponse resp) {
        resp.setStatus(302);
        resp.setHeader("Location", "/paymentError.html");
    }
}

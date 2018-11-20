package de.micromata.paypal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The url of this servlet should be configured in {@link PayPalConfig#getCancelUrl()}.
 * This is an example servlet for dealing with payment cancellations called by PayPal if the user
 * cancelled the payment process.
 */
public class PaymentCancelServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(PaymentCancelServlet.class);

    private static PayPalConfig config;

    public static void setConfig(PayPalConfig config) {
        PaymentCancelServlet.config = config;
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
        cancelPayment(req, resp, paymentId, payerId);
    }

    /**
     * Override this method for own processing of cancelled payments.
     *
     * @param req
     * @param resp
     * @param paymentId
     * @param payerId
     */
    protected void cancelPayment(HttpServletRequest req, HttpServletResponse resp, String paymentId, String payerId) throws IOException {
        log.info("Payment cancelled: paymentId=" + paymentId + ", PayerID=" + payerId);
        resp.setStatus(302);
        resp.setHeader("Location", "/paymentCancelled.html");
    }
}

package de.micromata.merlin.paypal.sdk;

import com.paypal.base.rest.APIContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PaymentCancelServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(PaymentCancelServlet.class);

    private APIContext apiContext;

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
    protected void cancelPayment(HttpServletRequest req, HttpServletResponse resp, String paymentId, String payerId) {
        log.info("Payment cancelled: paymentId=" + paymentId + ", PayerID=" + paymentId);
    }
}

package de.micromata.merlin.paypal.sdk;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PaymentReceiveServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(PaymentReceiveServlet.class);

    private APIContext apiContext;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Payment payment = new Payment();
        payment.setId(req.getParameter("paymentId"));
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(req.getParameter("PayerID"));
        executePayment(req, resp, payment, paymentExecution);
    }

    /**
     * Override this method for own processing of payments.
     * @param req
     * @param resp
     * @param payment
     * @param paymentExecution
     */
    protected void executePayment(HttpServletRequest req, HttpServletResponse resp, Payment payment, PaymentExecution paymentExecution) {
        log.info("Payment received: paymentId=" + payment.getId() + ", PayerID=" + paymentExecution.getPayerId());
        try {
            Payment createdPayment = payment.execute(apiContext, paymentExecution);
            log.info("Payment executed: " + createdPayment);
        } catch (PayPalRESTException e) {
            log.error("Error while receiving/executing payment: " + e.getDetails());
        }
    }
}

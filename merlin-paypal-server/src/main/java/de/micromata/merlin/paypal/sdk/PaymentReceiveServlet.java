package de.micromata.merlin.paypal.sdk;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import de.micromata.merlin.paypal.PaypalConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The url of this servlet should be configured in {@link PaypalConfig#getReturnUrl()}.
 */
public class PaymentReceiveServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(PaymentReceiveServlet.class);

    private static APIContext apiContext;

    public static void setAPIContext(APIContext apiContext) {
        PaymentReceiveServlet.apiContext = apiContext;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        if (apiContext == null) {
            log.error("Don't forget to set api context.");
        }
    }

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
        if (StringUtils.isBlank(payment.getId())) {
            log.error("Can't execute payment, paymentId not given. Aborting payment...");
            return;
        }
        if (StringUtils.isBlank(paymentExecution.getPayerId())) {
            log.error("Can't execute payment, payerId not given. Aborting payment...");
            return;
        }
        try {
            Payment createdPayment = payment.execute(apiContext, paymentExecution);
            log.info("Payment executed: " + createdPayment);
        } catch (PayPalRESTException e) {
            log.error("Error while receiving/executing payment: " + e.getDetails());
        }
    }
}

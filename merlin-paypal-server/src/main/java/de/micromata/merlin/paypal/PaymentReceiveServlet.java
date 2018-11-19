package de.micromata.merlin.paypal;

import de.micromata.merlin.paypal.data.PaymentApproveRequestInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The url of this servlet should be configured in {@link PayPalConfig#getReturnUrl()}.
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
        PaymentApproveRequestInfo approval = new PaymentApproveRequestInfo();
        approval.setPayerId(req.getParameter("PayerID"));
        try {
            executeApprovedPayment(req, resp, paymentId, approval);
        } catch (PayPalRestException ex) {
            log.error("Error while executing payment: " + ex.getMessage(), ex);
        }
    }

    /**
     * Override this method for own processing of payments.
     *
     * @param req
     * @param resp
     * @param paymentId
     * @param approval
     */
    protected void executeApprovedPayment(HttpServletRequest req, HttpServletResponse resp, String paymentId, PaymentApproveRequestInfo approval) throws IOException, PayPalRestException {
        log.info("Payment received: paymentId=" + paymentId + ", PayerID=" + approval.getPayerId());
        if (StringUtils.isBlank(paymentId)) {
            log.error("Can't execute payment, paymentId not given. Aborting payment...");
            redirectToErrorPage(resp);
            return;
        }
        if (StringUtils.isBlank(approval.getPayerId())) {
            log.error("Can't execute payment, payerId not given. Aborting payment...");
            redirectToErrorPage(resp);
            return;
        }
        PayPalConnector.executeApprovedPayment(config, paymentId, approval);
        /*
        try {
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            if (executedPayment != null) {
                resp.setStatus(302);
                resp.setHeader("Location", "/paymentExecuted.html");
                return;
            }
            log.info("Payment executed: " + executedPayment);
        } catch (PayPalRESTException e) {
            log.error("Error while receiving/executing payment: " + e.getDetails());
        }*/
        //redirectToErrorPage(resp);
    }

    private void redirectToErrorPage(HttpServletResponse resp) {
        resp.setStatus(302);
        resp.setHeader("Location", "/paymentError.html");
    }
}

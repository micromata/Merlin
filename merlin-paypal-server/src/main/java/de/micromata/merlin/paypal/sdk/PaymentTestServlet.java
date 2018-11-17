package de.micromata.merlin.paypal.sdk;

import com.paypal.api.payments.Transaction;
import de.micromata.merlin.paypal.PaypalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class PaymentTestServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(PaymentTestServlet.class);

    private static PaypalConfig paypalConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        if (paypalConfig == null) {
            log.error("Don't forget to set PaypalConfig.");
        }
    }

    public static void setPaypalConfig(PaypalConfig paypalConfig) {
        PaymentTestServlet.paypalConfig = paypalConfig;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String subtotal = req.getParameter("subtotal");
        String tax = req.getParameter("tax");
        String description = req.getParameter("description");
        PaymentAmount amount = new PaymentAmount(PaymentAmount.Currency.EUR).setSubtotal(asBigdecimal("subtotal", subtotal))
                .setTax(asBigdecimal("tax", tax));
        Transaction transaction = PaymentCreator.createTransaction(amount, description);
        PaymentCreator.publish(paypalConfig, transaction);
    }

    private BigDecimal asBigdecimal(String variable, String value) {
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (Exception ex) {
            log.error("Can't convert value '" + value + "' for variable '" + variable + "': " + ex.getMessage());
            return null;
        }
    }
}

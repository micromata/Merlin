package de.micromata.paypal;

import de.micromata.paypal.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is only a demo servlet showing how to create a payment. This is the first step of a payment process.
 */
public class PaymentTestServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(PaymentTestServlet.class);

    private static PayPalConfig paypalConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        if (paypalConfig == null) {
            log.error("Don't forget to set PayPalConfig.");
        }
    }

    public static void setPaypalConfig(PayPalConfig paypalConfig) {
        PaymentTestServlet.paypalConfig = paypalConfig;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String invoicenumber = req.getParameter("invoicenumber");
        String priceString = req.getParameter("price");
        String taxString = req.getParameter("tax");
        String description = req.getParameter("description");
        String itemDescription = req.getParameter("itemdescription");
        String noteToPayer = req.getParameter("notetopayer");
        double price = 9.99;
        double tax = 0;
        try {
            price = Double.valueOf(priceString);
        } catch (NumberFormatException ex) {
            log.error("Can't parse price string: " + priceString);
        }
        try {
            tax = Double.valueOf(taxString);
        } catch (NumberFormatException ex) {
            log.error("Can't parse price tax: " + taxString);
        }


        Payment payment = new Payment().setConfig(paypalConfig);
        Transaction transaction = new Transaction().setDescription(description);
        transaction.addItem(itemDescription, price);
        Details details = new Details();
        details.setTax(tax);
        transaction.createAmount(Currency.EUR, details);
        payment.addTransaction(transaction);
        if (Utils.isNotBlank(noteToPayer)) {
            payment.setNoteToPayer(noteToPayer);
        }
        payment.setShipping(ShippingPreference.NO_SHIPPING);

        PaymentCreated paymentCreated = null;
        try {
            paymentCreated = PayPalConnector.createPayment(paypalConfig, payment);
        } catch (PayPalRestException ex) {
            log.error("Error while executing payment: " + ex.getMessage(), ex);
            return;
        }
        String redirectUrl = paymentCreated.getPayPalUrlForUserPayment();
        if (Utils.isNotBlank(redirectUrl)) {
            resp.sendRedirect(redirectUrl);
        } else {
            resp.sendRedirect("/index.html");
        }
    }
}

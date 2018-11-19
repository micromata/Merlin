package de.micromata.merlin.paypal;

import de.micromata.merlin.paypal.data.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        Payment payment = new Payment().setConfig(paypalConfig);
        Transaction transaction = new Transaction();
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
        transaction.addItem(itemDescription, price);
        Details details = new Details();
        details.setTax(tax);
        transaction.createAmount(Amount.Currency.EUR, details);
        payment.addTransaction(transaction).setNoteToPayer(noteToPayer);
        payment.setShipping(ShippingPreference.NO_SHIPPING);

        PaymentExecution executionPayment = null;
        try {
            executionPayment = PayPalConnector.createPayment(paypalConfig, payment);
        } catch (PayPalRestException ex) {
            log.error("Error while executing payment: " + ex.getMessage(), ex);
            return;
        }
        String redirectUrl = executionPayment.getRedirectUserHref();
        if (StringUtils.isNotBlank(redirectUrl)) {
            resp.sendRedirect(redirectUrl);
        } else {
            resp.sendRedirect("/index.html");
        }
    }
}

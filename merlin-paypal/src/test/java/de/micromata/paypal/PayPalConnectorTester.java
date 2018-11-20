package de.micromata.paypal;

import de.micromata.paypal.data.*;

import java.io.File;

public class PayPalConnectorTester {

    public static void main(String[] args) throws Exception {
        File file = new File(System.getProperty("user.home"), ".merlin-paypal");
        PayPalConfig config = new PayPalConfig().read(file);
        //getAccessToken(config);
        createPayment(config);
    }

    private static void getAccessToken(PayPalConfig config) throws Exception {
        System.out.println(PayPalConnector.getAccessToken(config));
    }

    private static void createPayment(PayPalConfig config) throws Exception {
        Payment payment = new Payment().setConfig(config);
        Transaction transaction = new Transaction();
        transaction.addItem("Online Elections 2019", 29.99);
        Details details = new Details();
        details.setTax(5.70);
        transaction.createAmount(Currency.EUR, details);
        transaction.setInoviceNumber("1234");
        payment.addTransaction(transaction).setNoteToPayer("Enjoy your Elections with POLYAS.");
        //System.out.println(JsonUtils.toJson(payment, true));
        PaymentCreated paymentExecution = PayPalConnector.createPayment(config, payment);
    }
}
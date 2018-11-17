package de.micromata.merlin.paypal.sdk;

import com.paypal.api.payments.*;
import de.micromata.merlin.paypal.PaypalConfig;

import java.util.ArrayList;
import java.util.List;

public class PaymentCreator {
    public static Payment create(PaypalConfig config, Transaction... transactions) {
        // Set payer details
        Payer payer = new Payer();
        payer.setPaymentMethod(config.getDefaultPayment());

        // Set redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(config.getCancelUrl());
        redirectUrls.setReturnUrl(config.getReturnUrl());

        // Add transaction to a list
        List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionList.add(transaction);
        }

        // Add payment details
        Payment payment = new Payment();
        payment.setIntent(config.getDefaultIntent());
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactionList);
        return payment;
    }
}

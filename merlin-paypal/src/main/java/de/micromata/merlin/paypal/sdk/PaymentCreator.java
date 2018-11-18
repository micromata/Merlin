package de.micromata.merlin.paypal.sdk;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import de.micromata.merlin.paypal.PayPalConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentCreator {
    private static Logger log = LoggerFactory.getLogger(PaymentCreator.class);

    /**
     * Only with one item, quantity 1 and price equals to amount's subtotal.
     *
     * @param amount
     * @param invoiceNumber
     * @param description
     * @param itemDescription
     * @return
     */
    public static Transaction createTransaction(PaymentAmount amount, String invoiceNumber, String description, String itemDescription) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount.asAmount());
        if (StringUtils.isNotBlank(invoiceNumber)) {
            transaction.setInvoiceNumber(invoiceNumber);
        }
        transaction.setDescription(description);
        Item item = new Item();
        item.setName(itemDescription).setQuantity("1").setCurrency(amount.getCurrency()).setPrice(amount.getSubtotalString());
        List<Item> items = new ArrayList<>();
        items.add(item);

        ItemList itemList = new ItemList();
        itemList.setItems(items);

        transaction.setItemList(itemList);
        return transaction;
    }

    public static Payment prepare(PayPalConfig config, Transaction... transactions) {
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

    /**
     * Creates the remote payment (publish to Paypal).
     *
     * @param payment
     * @return Return Paypal's redirect url for the user to do the payment.
     */
    public static String publish(PayPalConfig config, Payment payment) {
        /*
        WebProfile webProfile = new WebProfile();
        InputFields inputFields = new InputFields();
        inputFields.setNoShipping(0);
        inputFields.setAddressOverride(1);
        webProfile.setInputFields(inputFields);
        try {
            webProfile.create(config.getApiContext());
        } catch (PayPalRESTException e) {
            log.error("PayPalRESTException occurred while trying to publish web profile: " + e.getDetails() + ". webProfile=" + webProfile);
            return null;
        }*/
        // Create payment
        try {
            Payment createdPayment = payment.create(config.getApiContext());
            if (createdPayment != null) {
                log.info("Created payment by PayPal: " + createdPayment);
            } else {
                log.error("Error while trying to publish payment: " + payment);
                return null;
            }
            Iterator<Links> links = createdPayment.getLinks().iterator();
            while (links.hasNext()) {
                Links link = links.next();
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    // Redirect the customer to link.getHref()
                    String redirectUserHref = link.getHref();
                    log.info("Redirect user to: " + redirectUserHref);
                    return redirectUserHref;
                }
            }
        } catch (PayPalRESTException e) {
            log.error("PayPalRESTException occurred while trying to publish payment: " + e.getDetails() + ". payment=" + payment);
            return null;
        }
        log.error("Oups, no redirect link found for redirecting the user.");
        return null;
    }

    public static String publish(PayPalConfig config, Transaction... transactions) {
        Payment payment = prepare(config, transactions);
        return publish(config, payment);
    }
}

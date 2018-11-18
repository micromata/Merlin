package de.micromata.merlin.paypal.sdk;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import de.micromata.merlin.paypal.PaypalConfig;
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
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        items.add(item);
        itemList.setItems(items);

        transaction.setItemList(itemList);
        return transaction;
    }

    public static Payment prepare(PaypalConfig config, Transaction... transactions) {
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
    public static String publish(PaypalConfig config, Payment payment) {
        // Create payment
        try {
            log.info("Publish payment: " + payment);
            Payment createdPayment = payment.create(config.getApiContext());
            log.info("Created payment by PayPal: " + createdPayment);
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
            log.error("PayPalRESTException occured while trying to publish payment: " + e.getDetails());
            return null;
        }
        log.error("Oups, no redirect link found for redirecting the user.");
        return null;
    }

    public static String publish(PaypalConfig config, Transaction... transactions) {
        Payment payment = prepare(config, transactions);
        return publish(config, payment);
    }
}

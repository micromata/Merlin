package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.PayPalConfig;
import de.micromata.paypal.PayPalConnector;
import de.micromata.paypal.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this class to prepare your payment. This payment will be sent to PayPal:
 * {@link PayPalConnector#createPayment(PayPalConfig, Payment)}
 */
public class Payment {
    private String intent = "sale";
    private Payer payer = new Payer();
    private List<Transaction> transactions = new ArrayList<>();
    private String noteToPayer;
    private ApplicationContext applicationContext;
    private RedirectUrls redirectUrls = new RedirectUrls();

    /**
     * Default is "sale".
     */
    public String getIntent() {
        return intent;
    }

    public Payer getPayer() {
        return payer;
    }

    public Payment addTransaction(Transaction transaction) {
        transactions.add(transaction);
        return this;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @JsonProperty(value = "note_to_payer")
    public String getNoteToPayer() {
        return noteToPayer;
    }

    /**
     * Ensures max length 165: https://developer.paypal.com/docs/api/payments/v1/#definition-transaction
     * @param noteToPayer
     * @return
     */
    public Payment setNoteToPayer(String noteToPayer) {
        this.noteToPayer = Utils.ensureMaxLength(noteToPayer, 165);
        return this;
    }

    @JsonProperty(value = "redirect_urls")
    public RedirectUrls getRedirectUrls() {
        return redirectUrls;
    }

    /**
     * This method is automatically called by {@link PayPalConnector#createPayment(PayPalConfig, Payment)} and
     * adds the return urls for PayPal.
     * @param config
     * @return
     */
    public Payment setConfig(PayPalConfig config) {
        redirectUrls.setConfig(config);
        return this;
    }

    /**
     * Is called internally before processing a payment for updating item currencies and calculating sums etc.
     */
    public void recalculate() {
        for (Transaction transaction : transactions) {
            transaction.getAmount().getDetails().calculateSubtotal(transaction);
            String currency = transaction.getAmount().getCurrency();
            for (Item item : transaction.getItemList().getItems()) {
                item.setCurrency(currency);
            }
        }
    }

    @JsonProperty(value = "application_context")
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Payment setShipping(ShippingPreference shippingPreference) {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
        applicationContext.setShippingPreference(shippingPreference);
        return this;
    }
}

package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.PayPalConfig;
import de.micromata.paypal.PayPalConnector;

import java.util.List;

/**
 * After executing a payment through {@link PayPalConnector#executePayment(PayPalConfig, String, String)}
 * PayPal responds with this object containing everything concerning the approved and executed Payment.
 */
public class PaymentExecuted {
    private String id, intent, state, cart, createTime;
    private Payer payer;
    private List<Transaction> transactions;
    private List<Link> links;
    private String origninalPayPalResponse;

    public String getId() {
        return id;
    }

    public String getIntent() {
        return intent;
    }

    public String getState() {
        return state;
    }

    public String getCart() {
        return cart;
    }

    @JsonProperty(value = "create_time")
    public String getCreateTime() {
        return createTime;
    }

    public Payer getPayer() {
        return payer;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Link> getLinks() {
        return links;
    }

    /**
     * @return the original response from PayPal. This object is generated from this json string.
     */
    public String getOrigninalPayPalResponse() {
        return origninalPayPalResponse;
    }

    public void setOrigninalPayPalResponse(String origninalPayPalResponse) {
        this.origninalPayPalResponse = origninalPayPalResponse;
    }
}

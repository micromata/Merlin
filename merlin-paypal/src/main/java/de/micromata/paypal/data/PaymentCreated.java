package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.PayPalConfig;
import de.micromata.paypal.PayPalConnector;

import java.util.List;

/**
 * Object returned by PayPal on calling {@link PayPalConnector#createPayment(PayPalConfig, Payment)}.
 * This payment is created and as a next step the user should process this payment via {@link #getPayPalUrlForUserPayment()}.
 */
public class PaymentCreated {
    private String id;
    private String state;
    private String createTime;
    private String intent;
    private Payer payer;
    private List<Transaction> transactions;
    private List<Link> links;
    private String noteToPayer;
    private String origninalPayPalResponse;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    @JsonProperty(value = "create_time")
    public String getCreateTime() {
        return createTime;
    }

    /**
     * Default is "sale".
     */
    public String getIntent() {
        return intent;
    }

    public Payer getPayer() {
        return payer;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @JsonProperty(value = "note_to_payer")
    public String getNoteToPayer() {
        return noteToPayer;
    }

    public List<Link> getLinks() {
        return links;
    }

    /**
     * After creating a payment on the PayPal servers, PayPal provides a url to redirect the user to for doing the
     * payment.
     * @return Redirect href provided by PayPal for the user to proceed with the payment.
     */
    public String getPayPalUrlForUserPayment() {
        if (links == null) {
            return null;
        }
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase("approval_url")) {
                // Redirect the customer to link.getHref()
                return link.getHref();
            }
        }
        return null;
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

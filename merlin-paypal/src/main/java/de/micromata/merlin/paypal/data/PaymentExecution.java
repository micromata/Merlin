package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.merlin.paypal.PayPalConfig;

import java.util.List;

/**
 * Object returned by PayPal on calling {@link de.micromata.merlin.paypal.PayPalConnector#createPayment(PayPalConfig, Payment)}.
 */
public class PaymentExecution {
    private String id;
    private String state;
    private String createTime;
    private String intent;
    private Payer payer;
    private List<Transaction> transactions;
    private List<Link> links;
    private String noteToPayer;

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

    public String getRedirectUserHref() {
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
}

package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.merlin.paypal.PayPalConfig;

/**
 * Only used as payload for {@link de.micromata.merlin.paypal.PayPalConnector#executeApprovedPayment(PayPalConfig, String, PaymentApproveRequestInfo)}
 */
public class PaymentApproveRequestInfo {
    private String payerId;

    @JsonProperty(value = "payer_id")
    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }
}

package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Will be set by PayPal.
 */
public class Payee {
    private String merchantId, email;

    @JsonProperty(value = "merchant_id")
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

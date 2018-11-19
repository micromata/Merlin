package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payee {
    // "payee": {
    //        "merchant_id": "HZTZJYXGGGR7E",
    //        "email": "verwaltung-facilitator@polyas.de"
    //      }
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

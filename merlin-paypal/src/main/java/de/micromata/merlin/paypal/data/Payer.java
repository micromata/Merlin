package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payer {
    private String paymentMethod = "paypal";
    private String status;
    private PayerInfo payerInfo;

    /**
     * Default is "paypal".
     * @return
     */
    @JsonProperty(value = "payment_method")
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    @JsonProperty(value = "payer_info")
    public PayerInfo getPayerInfo() {
        return payerInfo;
    }
}

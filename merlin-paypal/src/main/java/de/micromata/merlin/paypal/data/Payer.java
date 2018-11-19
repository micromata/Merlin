package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payer {
    private String paymentMethod = "paypal";

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
}

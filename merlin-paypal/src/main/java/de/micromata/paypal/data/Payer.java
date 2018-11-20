package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payer {
    public enum PaymentMethod {PAYPAL, CREDIT_CARD, PAY_UPON_INVOICE, CARRIER, ALTERNATE_PAYMENT, BANK}

    private String paymentMethod = "paypal";
    private String status;
    private PayerInfo payerInfo;

    /**
     * Default is "paypal".
     *
     * @return
     */
    @JsonProperty(value = "payment_method")
    public String getPaymentMethod() {
        return paymentMethod;
    }

    void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod.name().toLowerCase();
    }

    public String getStatus() {
        return status;
    }

    @JsonProperty(value = "payer_info")
    public PayerInfo getPayerInfo() {
        return payerInfo;
    }
}

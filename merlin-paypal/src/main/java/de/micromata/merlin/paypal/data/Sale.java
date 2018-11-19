package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Sale {
    // "sale": {
    //            "id": "4L985693HL293544W",
    //            "state": "completed",
    //            "amount": {
    //              "total": "35.69",
    //              "currency": "EUR",
    //              "details": {
    //                "subtotal": "29.99",
    //                "tax": "5.70"
    //              }
    //            },
    //            "payment_mode": "INSTANT_TRANSFER",
    //            "protection_eligibility": "ELIGIBLE",
    //            "protection_eligibility_type": "ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE",
    //            "transaction_fee": {
    //              "value": "1.03",
    //              "currency": "EUR"
    //            },
    //            "parent_payment": "PAY-7M735246AF694021KLPZOZKA",
    //            "create_time": "2018-11-19T17:02:45Z",
    //            "update_time": "2018-11-19T17:02:45Z",
    //            "links": [
    //              {
    //                "href": "https://api.sandbox.paypal.com/v1/payments/sale/4L985693HL293544W",
    //                "rel": "self",
    //                "method": "GET"
    //              },
    //              {
    //                "href": "https://api.sandbox.paypal.com/v1/payments/sale/4L985693HL293544W/refund",
    //                "rel": "refund",
    //                "method": "POST"
    //              },
    //              {
    //                "href": "https://api.sandbox.paypal.com/v1/payments/payment/PAY-7M735246AF694021KLPZOZKA",
    //                "rel": "parent_payment",
    //                "method": "GET"
    //              }
    private String id, state, paymentMode, protectionEligibility, projectionEligibilityType, parentPayment, createTime, updateTime;
    private Amount amount;
    private TransactionFee transactionFee;
    private List<Link> links;

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    @JsonProperty(value = "payment_mode")
    public String getPaymentMode() {
        return paymentMode;
    }

    @JsonProperty(value = "protection_eligibility")
    public String getProtectionEligibility() {
        return protectionEligibility;
    }

    @JsonProperty(value = "protection_eligibility_type")
    public String getProjectionEligibilityType() {
        return projectionEligibilityType;
    }

    @JsonProperty(value = "parent_payment")
    public String getParentPayment() {
        return parentPayment;
    }

    @JsonProperty(value = "create_time")
    public String getCreateTime() {
        return createTime;
    }

    @JsonProperty(value = "update_time")
    public String getUpdateTime() {
        return updateTime;
    }

    public Amount getAmount() {
        return amount;
    }

    @JsonProperty(value = "transaction_fee")
    public TransactionFee getTransactionFee() {
        return transactionFee;
    }

    public List<Link> getLinks() {
        return links;
    }
}

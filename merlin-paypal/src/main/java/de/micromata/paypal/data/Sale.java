package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Sale {
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

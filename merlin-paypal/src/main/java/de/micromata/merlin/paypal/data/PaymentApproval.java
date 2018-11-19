package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PaymentApproval {
    private String id, intent, state, cart, createTime;
    private Payer payer;
    private List<Transaction> transactions;
    private List<Link> links;

    public String getId() {
        return id;
    }

    public String getIntent() {
        return intent;
    }

    public String getState() {
        return state;
    }

    public String getCart() {
        return cart;
    }

    @JsonProperty(value = "create_time")
    public String getCreateTime() {
        return createTime;
    }

    public Payer getPayer() {
        return payer;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Link> getLinks() {
        return links;
    }
}

package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExecutionPayment {
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

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    @JsonProperty(value = "create_time")
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Default is "sale".
     */
    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Payer getPayer() {
        return payer;
    }

    public ExecutionPayment addTransaction(Transaction transaction) {
        transactions.add(transaction);
        return this;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @JsonProperty(value = "note_to_payer")
    public String getNoteToPayer() {
        return noteToPayer;
    }

    public ExecutionPayment setNoteToPayer(String noteToPayer) {
        this.noteToPayer = noteToPayer;
        return this;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}

package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Transaction {
    private Amount amount;
    private String inoviceNumber;
    private String description;
    private Payee payee;
    private ItemList itemList = new ItemList();

    /**
     * Amount class is created and assigned to this transaction.
     * @param currency
     * @param details
     * @return
     */
    public Transaction createAmount(Amount.Currency currency, Details details) {
        amount = new Amount(currency);
        amount.setDetails(details);
        return this;
    }

    public Item addItem(String name, BigDecimal price) {
        Item item = new Item().setPrice(price).setName(name);
        itemList.add(item);
        return item;
    }

    public Item addItem(String name, double price) {
        Item item = new Item().setPrice(price).setName(name);
        itemList.add(item);
        return item;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @JsonProperty(value = "invoice_number")
    public String getInoviceNumber() {
        return inoviceNumber;
    }

    public void setInoviceNumber(String inoviceNumber) {
        this.inoviceNumber = inoviceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("item_list")
    public ItemList getItemList() {
        return itemList;
    }

    public void setItemList(ItemList itemList) {
        this.itemList = itemList;
    }

    public Payee getPayee() {
        return payee;
    }
}

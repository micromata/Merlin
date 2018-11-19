package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.merlin.paypal.utils.PayPalUtils;

import java.math.BigDecimal;
import java.util.List;

public class Transaction {
    private Amount amount;
    private String inoviceNumber;
    private String description;
    private Payee payee;
    private ItemList itemList = new ItemList();
    private List<RelatedResource> relatedResources;

    /**
     * Amount class is created and assigned to this transaction.
     *
     * @param currency
     * @param details
     * @return
     */
    public Transaction createAmount(Currency currency, Details details) {
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

    /**
     * Ensures maximum length of 127: https://developer.paypal.com/docs/api/payments/v1/#definition-transaction
     *
     * @param inoviceNumber
     */
    public void setInoviceNumber(String inoviceNumber) {
        this.inoviceNumber = PayPalUtils.ensureMaxLength(inoviceNumber, 127);
    }

    public String getDescription() {
        return description;
    }

    /**
     * Ensures maximum length of 127: https://developer.paypal.com/docs/api/payments/v1/#definition-transaction
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = PayPalUtils.ensureMaxLength(description, 127);
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

    @JsonProperty(value = "related_resources")
    public List<RelatedResource> getRelatedResources() {
        return relatedResources;
    }
}

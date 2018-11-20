package de.micromata.paypal.data;

import de.micromata.paypal.Utils;

import java.math.BigDecimal;

public class Item {
    private String name;
    private int quantity = 1;
    private BigDecimal price;
    private String currency;

    public String getName() {
        return name;
    }

    /**
     * Ensures maximum length of 127: https://developer.paypal.com/docs/api/payments/v1/#definition-item
     * @param name
     * @return
     */
    public Item setName(String name) {
        this.name = Utils.ensureMaxLength(name, 127);
        return this;
    }

    /**
     * Defaut is 1.
     *
     * @return
     */
    public int getQuantity() {
        return quantity;
    }

    public Item setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Item setPrice(BigDecimal price) {
        this.price = Utils.roundAmount(price);
        return this;
    }

    public Item setPrice(double price) {
        return setPrice(new BigDecimal(price));
    }

    public String getCurrency() {
        return currency;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }
}

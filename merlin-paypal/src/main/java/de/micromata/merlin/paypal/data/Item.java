package de.micromata.merlin.paypal.data;

import de.micromata.merlin.paypal.utils.PayPalUtils;

import java.math.BigDecimal;

public class Item {
    private String name;
    private int quantity = 1;
    private BigDecimal price;
    private String currency;

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
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
        this.price = PayPalUtils.roundAmount(price);
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

package de.micromata.merlin.paypal.data;

import java.math.BigDecimal;

public class Amount {
    public enum Currency {EUR, USD}

    private String currency;
    private Details details;

    public Amount() {
        currency = "USD";
    }

    public Amount(String currency) {
        this.currency = currency;
    }

    public Amount(Currency currency) {
        setCurrency(currency);
    }

    public Details getDetails() {
        return details;
    }

    public Amount setDetails(Details details) {
        this.details = details;
        return this;
    }

    public BigDecimal getTotal() {
        if (details == null) {
            return BigDecimal.ZERO;
        }
        return details.getTotal();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency.name();
    }

}

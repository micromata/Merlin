package de.micromata.paypal.data;

import java.math.BigDecimal;

public class TransactionFee {
    private BigDecimal value;
    private String currency;

    public BigDecimal getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }
}

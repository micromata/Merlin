package de.micromata.merlin.paypal.data;

import java.math.BigDecimal;

public class TransactionFee {
    //            "transaction_fee": {
    //              "value": "1.03",
    //              "currency": "EUR"
    //            },
    private BigDecimal value;
    private String currency;

    public BigDecimal getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }
}

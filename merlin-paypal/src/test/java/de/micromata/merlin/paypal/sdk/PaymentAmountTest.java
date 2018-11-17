package de.micromata.merlin.paypal.sdk;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentAmountTest {
    @Test
    void paymentCreationTest() {
        PaymentAmount amount = new PaymentAmount(PaymentAmount.Currency.EUR);
        assertEquals("0.00", amount.getTotalString());
        amount.setSubtotal(new BigDecimal("29.99"));
        assertEquals("29.99", amount.getTotalString());
        amount.setShipping(new BigDecimal("3.99"));
        assertEquals("33.98", amount.getTotalString());
        amount.setTax(new BigDecimal("5.70"));
        assertEquals("39.68", amount.getTotalString());

        amount.setTax(null);
        assertEquals("33.98", amount.getTotalString());
    }
}

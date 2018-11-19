package de.micromata.merlin.paypal;

import de.micromata.merlin.paypal.data.Amount;
import de.micromata.merlin.paypal.data.Details;
import de.micromata.merlin.paypal.data.Payment;
import de.micromata.merlin.paypal.data.Transaction;
import de.micromata.merlin.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest {

    @Test
    void transactionTest() {
        PayPalConfig config = PayPalConfig.createDemoConfig();
        Payment payment = new Payment().setConfig(config);
        Transaction transaction = new Transaction();
        transaction.addItem("Online Elections 2019", 29.99);
        Details details = new Details();
        details.setTax(5.70);
        transaction.createAmount(Amount.Currency.EUR, details);
        transaction.setInoviceNumber("1234");
        payment.addTransaction(transaction).setNoteToPayer("Enjoy your Elections with POLYAS.");
        assertEquals("35.69", transaction.getAmount().getTotal().toString());
        System.out.println(JsonUtils.toJson(payment, true));
    }

    @Test
    void paymentCreationTest() {
/*        Amount amount = new Amount(Amount.Currency.EUR);
        assertEquals("0.00", amount.getTotalString());
        amount.setSubtotal(new BigDecimal("29.99"));
        assertEquals("29.99", amount.getTotalString());
        amount.setShipping(new BigDecimal("3.99"));
        assertEquals("33.98", amount.getTotalString());
        amount.setTax(new BigDecimal("5.70"));
        assertEquals("39.68", amount.getTotalString());

        amount.setTax(null);
        assertEquals("33.98", amount.getTotalString());*/
    }
}
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
    void paymentTest() {
        PayPalConfig config = PayPalConfig.createDemoConfig();
        Payment payment = new Payment();
        Transaction transaction = new Transaction();
        transaction.addItem("Online Elections 2019", 29.99);
        Details details = new Details().setTax(5.70);
        transaction.createAmount(Amount.Currency.EUR, details);
        transaction.setInoviceNumber("1234");
        payment.addTransaction(transaction).setNoteToPayer("Enjoy your Elections with POLYAS.");
        payment.setConfig(config);
        payment.recalculate();
        assertEquals("35.69", transaction.getAmount().getTotal().toString());
        System.out.println(JsonUtils.toJson(payment, true));
    }
}
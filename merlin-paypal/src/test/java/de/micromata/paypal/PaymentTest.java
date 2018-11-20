package de.micromata.paypal;

import de.micromata.paypal.data.Currency;
import de.micromata.paypal.data.Details;
import de.micromata.paypal.data.Payment;
import de.micromata.paypal.data.Transaction;
import de.micromata.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest {

    @Test
    void paymentTest() {
        PayPalConfig config = PayPalConfig.createDemoConfig();
        Payment payment = new Payment();
        Transaction transaction = new Transaction();
        transaction.setInoviceNumber(generateString(200));
        assertEquals(127, transaction.getInoviceNumber().length());
        assertEquals(generateString(124) + "...", transaction.getInoviceNumber());
        transaction.addItem("Online Elections 2019", 29.99);
        Details details = new Details().setTax(5.70);
        transaction.createAmount(Currency.EUR, details);
        payment.addTransaction(transaction).setNoteToPayer("Enjoy your Elections with POLYAS.");
        payment.setConfig(config);
        payment.recalculate();
        assertEquals("35.69", transaction.getAmount().getTotal().toString());
        System.out.println(JsonUtils.toJson(payment, true));
    }

    private String generateString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(i % 10);
        }
        return sb.toString();
    }
}
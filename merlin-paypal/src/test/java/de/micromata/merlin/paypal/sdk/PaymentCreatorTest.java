package de.micromata.merlin.paypal.sdk;


import de.micromata.merlin.paypal.data.Amount;
import de.micromata.merlin.paypal.data.Details;
import de.micromata.merlin.paypal.PayPalConfig;
import de.micromata.merlin.paypal.data.Transaction;
import org.junit.jupiter.api.Test;

public class PaymentCreatorTest {
    @Test
    void paymentCreationTest() {
        PayPalConfig config = new PayPalConfig();
        config.setClientId("testClientId");
        config.setClientSecret("mySecret");
        config.setReturnUrl(PayPalConfig.DEMO_RETURN_URL);
        config.setCancelUrl(PayPalConfig.DEMO_CANCEL_URL);
        // Transaction information
        Transaction transaction = new Transaction();
        transaction.addItem("My test item", 29.99);
        Details details = new Details().setShipping(1.99).setTax(1.10);
        transaction.createAmount(Amount.Currency.EUR, details);
        transaction.setDescription("This is the payment transaction description.");

        //Payment payment = PaymentCreator.prepare(config, transaction);
    }

}

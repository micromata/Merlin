package de.micromata.merlin.paypal.sdk;


import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import de.micromata.merlin.paypal.PayPalConfig;
import org.junit.jupiter.api.Test;

public class PaymentCreatorTest {
    @Test
    void paymentCreationTest() {
        PayPalConfig config = new PayPalConfig();
        config.setClientId("testClientId");
        config.setClientSecret("mySecret");
        config.setReturnUrl(PayPalConfig.DEMO_RETURN_URL);
        config.setCancelUrl(PayPalConfig.DEMO_CANCEL_URL);
        // Set payment details
        Details details = new Details();
        details.setShipping("1");
        details.setSubtotal("5");
        details.setTax("1");

        // Payment amount
        Amount amount = new Amount();
        amount.setCurrency("USD");
        // Total must be equal to sum of shipping, tax and subtotal.
        amount.setTotal("7");
        amount.setDetails(details);

        // Transaction information
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("This is the payment transaction description.");

        Payment payment = PaymentCreator.prepare(config, transaction);
    }

}

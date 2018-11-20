package de.micromata.paypal.data;

import de.micromata.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentExecutionTest {
    private static Logger log = LoggerFactory.getLogger(PaymentExecutionTest.class);

    @Test
    void deserializeTest() {
        String json = "{\"id\":\"PAY-33V64607PB867972ALPZNWYQ\",\"intent\":\"sale\",\"state\":\"created\",\"payer\":{\"payment_method\":\"paypal\"},\"transactions\":[{\"amount\":{\"total\":\"35.69\",\"currency\":\"EUR\",\"details\":{\"subtotal\":\"29.99\",\"tax\":\"5.70\"}},\"invoice_number\":\"1234\",\"item_list\":{\"items\":[{\"name\":\"Online Elections 2019\",\"price\":\"29.99\",\"currency\":\"EUR\",\"quantity\":1}]},\"related_resources\":[]}],\"note_to_payer\":\"Enjoy your Elections with POLYAS.\",\"create_time\":\"2018-11-19T15:48:50Z\",\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v1/payments/payment/PAY-33V64607PB867972ALPZNWYQ\",\"rel\":\"self\",\"method\":\"GET\"},{\"href\":\"https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-4YG93186LD8174900\",\"rel\":\"approval_url\",\"method\":\"REDIRECT\"},{\"href\":\"https://api.sandbox.paypal.com/v1/payments/payment/PAY-33V64607PB867972ALPZNWYQ/execute\",\"rel\":\"execute\",\"method\":\"POST\"}]}\n";
        PaymentCreated payment = JsonUtils.fromJson(PaymentCreated.class, json);
        assertEquals("PAY-33V64607PB867972ALPZNWYQ", payment.getId());
        assertEquals(new BigDecimal("35.69"), payment.getTransactions().get(0).getAmount().getTotal());
        log.debug(JsonUtils.toJson(payment));
    }
}

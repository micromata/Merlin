package de.micromata.paypal;

import de.micromata.paypal.data.PaymentExecuted;
import de.micromata.paypal.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentApprovalTest {

    @Test
    void paymentTest() {
        String json = "{\"id\":\"PAY-7M735246AF694021KLPZOZKA\",\"intent\":\"sale\",\"state\":\"approved\",\"cart\":\"8D4582615J243054U\",\"payer\":{\"payment_method\":\"paypal\",\"status\":\"VERIFIED\",\"payer_info\":{\"email\":\"k.reinhard-buyer@me.com\",\"first_name\":\"test\",\"last_name\":\"buyer\",\"payer_id\":\"9KV4FWVA79N94\",\"shipping_address\":{\"recipient_name\":\"test buyer\",\"line1\":\"ESpachstr. 1\",\"city\":\"Freiburg\",\"state\":\"Empty\",\"postal_code\":\"79111\",\"country_code\":\"DE\"},\"country_code\":\"DE\"}},\"transactions\":[{\"amount\":{\"total\":\"35.69\",\"currency\":\"EUR\",\"details\":{\"subtotal\":\"29.99\",\"tax\":\"5.70\"}},\"payee\":{\"merchant_id\":\"HZTZJYXGGGR7E\",\"email\":\"verwaltung-facilitator@polyas.de\"},\"item_list\":{\"items\":[{\"name\":\"Elections 2019\",\"price\":\"29.99\",\"currency\":\"EUR\",\"quantity\":1}],\"shipping_address\":{\"recipient_name\":\"test buyer\",\"line1\":\"ESpachstr. 1\",\"city\":\"Freiburg\",\"state\":\"Empty\",\"postal_code\":\"79111\",\"country_code\":\"DE\"}},\"related_resources\":[{\"sale\":{\"id\":\"4L985693HL293544W\",\"state\":\"completed\",\"amount\":{\"total\":\"35.69\",\"currency\":\"EUR\",\"details\":{\"subtotal\":\"29.99\",\"tax\":\"5.70\"}},\"payment_mode\":\"INSTANT_TRANSFER\",\"protection_eligibility\":\"ELIGIBLE\",\"protection_eligibility_type\":\"ITEM_NOT_RECEIVED_ELIGIBLE,UNAUTHORIZED_PAYMENT_ELIGIBLE\",\"transaction_fee\":{\"value\":\"1.03\",\"currency\":\"EUR\"},\"parent_payment\":\"PAY-7M735246AF694021KLPZOZKA\",\"create_time\":\"2018-11-19T17:02:45Z\",\"update_time\":\"2018-11-19T17:02:45Z\",\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v1/payments/sale/4L985693HL293544W\",\"rel\":\"self\",\"method\":\"GET\"},{\"href\":\"https://api.sandbox.paypal.com/v1/payments/sale/4L985693HL293544W/refund\",\"rel\":\"refund\",\"method\":\"POST\"},{\"href\":\"https://api.sandbox.paypal.com/v1/payments/payment/PAY-7M735246AF694021KLPZOZKA\",\"rel\":\"parent_payment\",\"method\":\"GET\"}]}}]}],\"create_time\":\"2018-11-19T17:02:46Z\",\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v1/payments/payment/PAY-7M735246AF694021KLPZOZKA\",\"rel\":\"self\",\"method\":\"GET\"}]}";

        PaymentExecuted approval = JsonUtils.fromJson(PaymentExecuted.class, json);
        assertEquals("35.69", approval.getTransactions().get(0).getAmount().getTotal().toString());
    }
}
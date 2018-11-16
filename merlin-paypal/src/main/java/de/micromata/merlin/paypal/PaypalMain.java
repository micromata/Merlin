package de.micromata.merlin.paypal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class PaypalMain {
    private static Logger log = LoggerFactory.getLogger(PaypalMain.class);

    public static void main(String[] args) throws IOException {
        PaypalCredentials credentials = new PaypalCredentials();
        credentials.read(new File(System.getProperty("user.home"), ".merlin-paypal"));
        log.info("AppId=" + credentials.getAppId());
        log.info("PayPalPost=" + new PaypalPost().toJson(new BigDecimal("7.42")));
        restCall(credentials);
    }

    private static void restCall(PaypalCredentials credentials) {
        HttpsCall request = new HttpsCall().setBearerAuthorization(credentials.getAccessToken());
        PaypalPost paypalPost = new PaypalPost();
        paypalPost.setReturnUrl("https://example.com/your_redirect_url.html");
        paypalPost.setCancelUrl("https://example.com/your_cancel_url.html");
        String input = paypalPost.toJson(new BigDecimal("7.42"));
        String result = request.post("https://api.sandbox.paypal.com/v1/payments/payment ", input);
        log.info(result);
    }
}

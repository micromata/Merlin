package de.micromata.merlin.paypal;

import de.micromata.merlin.paypal.data.Payment;
import de.micromata.merlin.paypal.json.JsonUtils;
import de.micromata.merlin.paypal.purejava.HttpsCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayPalConnector {
    private static Logger log = LoggerFactory.getLogger(PayPalConnector.class);

    // "access_token":"<access token>"
    private static Pattern PATTERN_ACCESS_TOKEN = Pattern.compile("\"access_token\":\"([^\"]*)\"");

    public static String createPayment(PayPalConfig config, Payment payment) {
        if (log.isDebugEnabled()) {
            log.debug("Create payment: " + JsonUtils.toJson(payment, true));
        }
        String url = getUrl(config, "/v1/payments/payment");
        return executeCall(config, url, JsonUtils.toJson(payment));
    }

    /**
     * curl - v https:api.sandbox.paypal.com/v1/oauth2/token -H "Accept: application/json" -H "Accept-Language: en_US"
     * -u "<client_id>:<secret>" -d "grant_type=client_credentials"
     */
    public static String getAccessToken(PayPalConfig config) {
        String url = getUrl(config, "/v1/oauth2/token");
        String response = executeCall(config, url, "grant_type=client_credentials");
        // "access_token":"<access token>"
        Matcher matcher = PATTERN_ACCESS_TOKEN.matcher(response);
        if (!matcher.find()) {
            System.err.println("Didn't get access token from server: " + response);
            return null;
        }
        String accessToken = matcher.group(1);
        if (log.isDebugEnabled()) log.debug("Access token: " + accessToken);
        return accessToken;
    }


    private static String executeCall(PayPalConfig config, String url, String payload) {
        HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
        call.setUserPasswordAuthorization(config.getClientId() + ":" + config.getClientSecret());
        return call.post(url, payload);
    }

    private static String getUrl(PayPalConfig config, String url) {
        return "https://api.sandbox.paypal.com" + url;
    }
}

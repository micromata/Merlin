package de.micromata.merlin.paypal;

import de.micromata.merlin.paypal.data.PaymentApproval;
import de.micromata.merlin.paypal.data.PaymentExecution;
import de.micromata.merlin.paypal.data.Payment;
import de.micromata.merlin.paypal.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayPalConnector {
    private static Logger log = LoggerFactory.getLogger(PayPalConnector.class);

    // "access_token":"<access token>"
    private static Pattern PATTERN_ACCESS_TOKEN = Pattern.compile("\"access_token\":\"([^\"]*)\"");

    public static PaymentExecution createPayment(PayPalConfig config, Payment payment) throws PayPalRestException {
        try {
            String url = getUrl(config, "/v1/payments/payment");
            payment.recalculate();
            log.info("Create payment: " + JsonUtils.toJson(payment));
            String response = executeCall(config, url, JsonUtils.toJson(payment));
            PaymentExecution executionPayment = JsonUtils.fromJson(PaymentExecution.class, response);
            if (executionPayment == null) {
                throw new PayPalRestException("Error while creating payment: " + response);
            }
            log.info("Created execution payment: " + JsonUtils.toJson(executionPayment));
            return executionPayment;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while creating payment.", ex);
        }
    }

    public static void executeApprovedPayment(PayPalConfig config, String payementId, PaymentApproval paymentApproval) throws PayPalRestException {
        try {
            String url = getUrl(config, "/v1/payments/payment/" + payementId + "/execute");
            log.info("Aprove payment: paymentId=" + payementId + ", payerId=" + paymentApproval.getPayerId());
            String response = executeCall(config, url, JsonUtils.toJson(paymentApproval));
            log.info(response);
/*            PaymentExecution executionPayment = JsonUtils.fromJson(PaymentExecution.class, response);
            if (executionPayment == null) {
                throw new PayPalRestException("Error while creating payment: " + response);
            }
            log.info("Created execution payment: " + JsonUtils.toJson(executionPayment));
            return executionPayment;*/
        } catch (Exception ex) {
            throw new PayPalRestException("Error while creating payment.", ex);
        }
    }

    /**
     * curl - v https:api.sandbox.paypal.com/v1/oauth2/token -H "Accept: application/json" -H "Accept-Language: en_US"
     * -u "<client_id>:<secret>" -d "grant_type=client_credentials"
     */
    public static String getAccessToken(PayPalConfig config) throws PayPalRestException {
        try {
            String url = getUrl(config, "/v1/oauth2/token");
            HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
            call.setUserPasswordAuthorization(config.getClientId() + ":" + config.getClientSecret());
            String response = call.post(url, "grant_type=client_credentials");
            // "access_token":"<access token>"
            Matcher matcher = PATTERN_ACCESS_TOKEN.matcher(response);
            if (!matcher.find()) {
                log.error("Can't get access token from server: " + response);
                throw new PayPalRestException("Can't get access token from server: " + response);
            }
            String accessToken = matcher.group(1);
            if (log.isDebugEnabled()) log.debug("Access token: " + accessToken);
            return accessToken;
        } catch (Exception ex) {
            throw new PayPalRestException("Error while creating payment.", ex);
        }
    }


    private static String executeCall(PayPalConfig config, String url, String payload) throws IOException, MalformedURLException {
        return executeCall(config, url, payload, null);
    }

    private static String executeCall(PayPalConfig config, String url, String payload, String accessToken) throws IOException, MalformedURLException {
        HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
        if (accessToken != null) {
            call.setBearerAuthorization(accessToken);
        } else {
            call.setUserPasswordAuthorization(config.getClientId() + ":" + config.getClientSecret());
        }
        call.setContentType(HttpsCall.MimeType.JSON);
        return call.post(url, payload);
    }

    private static String getUrl(PayPalConfig config, String url) {
        if (config.getMode() == PayPalConfig.Mode.SANDBOX) {
            return "https://api.sandbox.paypal.com" + url;
        } else {
            return "https://api.paypal.com" + url;
        }
    }
}

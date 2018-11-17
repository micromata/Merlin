package de.micromata.merlin.paypal.purejava;

import de.micromata.merlin.paypal.PaypalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessToken {
    private static Logger log = LoggerFactory.getLogger(AccessToken.class);

    // "access_token":"<access token>"
    private static Pattern PATTERN_ACCESS_TOKEN = Pattern.compile("\"access_token\":\"([^\"]*)\"");

    /**
     * curl - v https:api.sandbox.paypal.com/v1/oauth2/token -H "Accept: application/json" -H "Accept-Language: en_US"
     * -u "<client_id>:<secret>" -d "grant_type=client_credentials"
     */
    public static String getAccessToken(PaypalConfig credentials) {
        HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
        call.setUserPasswordAuthorization(credentials.getClientId() + ":" + credentials.getClientSecret());
        String response = call.post("https://api.sandbox.paypal.com/v1/oauth2/token", "grant_type=client_credentials");
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
}

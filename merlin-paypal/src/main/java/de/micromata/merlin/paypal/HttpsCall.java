package de.micromata.merlin.paypal;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

/**
 * Helper class for handling https callss.
 */
public class HttpsCall {
    private static Logger log = LoggerFactory.getLogger(HttpsCall.class);

    public enum MimeType {JSON}

    private String authorization;
    private String acceptLanguage;
    private MimeType contentType;
    private MimeType accept;

    /**
     * @param urlString Https url to connect.
     * @param input     The post input.
     * @return The result from the remote server.
     */
    public String post(String urlString, String input) {
        try {
            if (log.isDebugEnabled()) log.debug("Call '" + urlString + "' with input: " + input);
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            if (authorization != null) {
                conn.setRequestProperty("Authorization", authorization);
            }
            if (acceptLanguage != null) {
                if (log.isDebugEnabled()) log.debug("Accept-Language: " + acceptLanguage);
                conn.setRequestProperty("Accept-Language", acceptLanguage);
            }
            if (contentType != null) {
                if (log.isDebugEnabled()) log.debug("Content-Type: application/json");
                conn.setRequestProperty("Content-Type", "application/json");
            }
            if (accept != null) {
                if (log.isDebugEnabled()) log.debug("Accept: application/json");
                conn.setRequestProperty("Accept", "application/json");
            }
            CreatePaymentData paypalPost = new CreatePaymentData();
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
                    conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            StringWriter out = new StringWriter();
            IOUtils.copy(new InputStreamReader(conn.getInputStream()), out);
            conn.disconnect();
            if (log.isDebugEnabled()) log.debug(out.toString());
            return out.toString();
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param accessToken Sets authorization to "Bearer &lt;accessToken&gt;"
     * @return this for chaining.
     */
    public HttpsCall setBearerAuthorization(String accessToken) {
        if (log.isDebugEnabled()) {
            log.debug("Authorization: Bearer " + accessToken);
        }
        this.authorization = "Bearer " + accessToken;
        return this;
    }

    /**
     * @param usernamePassword &lt;username&gt;:&lt;password&gt;
     * @return this for chaining.
     */
    public HttpsCall setUserPasswordAuthorization(String usernamePassword) {
        if (log.isDebugEnabled()) {
            if (usernamePassword.length() < 10) {
                log.debug("Authorization: TO-SHORT?: " + usernamePassword.length());
            }
            log.debug("Authorization: Basic " + usernamePassword.substring(0, 3) + "...:..." + usernamePassword.substring(usernamePassword.length() - 3));
        }
        this.authorization = "Basic " + new String(Base64.getEncoder().encode(usernamePassword.getBytes()));
        return this;
    }

    public HttpsCall setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
        return this;
    }

    public HttpsCall setContentType(MimeType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpsCall setAccept(MimeType accept) {
        this.accept = accept;
        return this;
    }
}

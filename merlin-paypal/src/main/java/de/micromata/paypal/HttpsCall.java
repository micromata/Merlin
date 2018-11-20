package de.micromata.paypal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

/**
 * Helper class for handling https calls.
 * This class has no external dependencies.
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
    public String post(String urlString, String input) throws MalformedURLException, IOException {
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
        OutputStream os = conn.getOutputStream();
        os.write(input.getBytes());
        os.flush();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
                conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }
        StringWriter out = new StringWriter();


        copy(new InputStreamReader(conn.getInputStream()), out);
        conn.disconnect();
        if (log.isDebugEnabled()) log.debug("Response: " + out.toString());
        return out.toString();
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

    private static final int BUFFER_SIZE = 4 * 1024;

    private void copy(final Reader input, final Writer output) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }
}

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

/**
 * Helper class for handling https callss.
 */
public class HttpsCall {
    private static Logger log = LoggerFactory.getLogger(HttpsCall.class);

    public enum ContentType {JSON}

    private String authorization;
    private ContentType contentType = ContentType.JSON;

    /**
     * @param urlString Https url to connect.
     * @param input The post input.
     * @return The result from the remote server.
     */
    public String post(String urlString, String input) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            if (authorization != null) {
                conn.setRequestProperty("Authorization", authorization);
            }
            conn.setRequestProperty("Content-Type", "application/json");
            PaypalPost paypalPost = new PaypalPost();
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            StringWriter out = new StringWriter();
            IOUtils.copy(new InputStreamReader(conn.getInputStream()), out);
            conn.disconnect();
            return out.toString();
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     *
     * @param accessToken Sets authorization to "Bearer &lt;accessToken&gt;"
     * @return this for chaining.
     */
    public HttpsCall setBearerAuthorization(String accessToken) {
        this.authorization = "Bearer " + accessToken;
        return this;
    }
}

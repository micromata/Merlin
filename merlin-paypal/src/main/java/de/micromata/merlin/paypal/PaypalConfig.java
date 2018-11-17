package de.micromata.merlin.paypal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PaypalConfig {
    static final String KEY_CLIENT_ID = "paypal.client_id";
    static final String KEY_SECRET = "paypal.secret";
    static final String KEY_RETURN_URL = "paypal.return_url";
    static final String KEY_CANCEL_URL = "paypal.cancel_url";
    public static final String DEMO_RETURN_URL = "https://example.com/your_redirect_url.html";
    public static final String DEMO_CANCEL_URL = "https://example.com/your_cancel_url.html";

    private String clientId;
    private String secret;
    private String returnUrl;
    private String cancelUrl;
    private String defaultPayment = "paypal";
    private String defaultIntent = "sale";

    public PaypalConfig() {
    }

    public void read(File propertiesFile) throws IOException {
        Properties props = new Properties();
        props.load(new FileReader(propertiesFile));
        read(props);
    }

    public void read(Properties props) {
        clientId = props.getProperty(KEY_CLIENT_ID);
        secret = props.getProperty(KEY_SECRET);
        returnUrl = props.getProperty(KEY_RETURN_URL);
        cancelUrl = props.getProperty(KEY_CANCEL_URL);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    /**
     * The payment used as default if no other is set.
     * @return "paypal" as default.
     */

    public String getDefaultPayment() {
        return defaultPayment;
    }

    public void setDefaultPayment(String defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    /**
     * The default intent for creating payments.
     * @return "sale" as default.
     * @see com.paypal.api.payments.Payment#setIntent(String)
     */
    public String getDefaultIntent() {
        return defaultIntent;
    }

    public void setDefaultIntent(String defaultIntent) {
        this.defaultIntent = defaultIntent;
    }
}

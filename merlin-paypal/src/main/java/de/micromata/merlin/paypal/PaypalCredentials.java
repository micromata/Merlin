package de.micromata.merlin.paypal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PaypalCredentials {
    static final String KEY_CLIENT_ID = "paypal.client_id";
    static final String KEY_SECRET = "paypal.secret";
    private String clientId;
    private String secret;

    public PaypalCredentials() {
    }

    public void read(File propertiesFile) throws IOException {
        Properties props = new Properties();
        props.load(new FileReader(propertiesFile));
        read(props);
    }

    public void read(Properties props) {
        clientId = props.getProperty(KEY_CLIENT_ID);
        secret = props.getProperty(KEY_SECRET);
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
}

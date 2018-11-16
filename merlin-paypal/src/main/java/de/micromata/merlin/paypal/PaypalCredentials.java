package de.micromata.merlin.paypal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PaypalCredentials {
    private static final String KEY_ACCESS_TOKEN = "paypal.access_token";
    private static final String KEY_APP_ID = "paypal.app_id";
    private String accessToken;
    private String appId;

    public PaypalCredentials() {
    }

    public void read(File propertiesFile) throws IOException {
        Properties props = new Properties();
        props.load(new FileReader(propertiesFile));
        read(props);
    }

    public void read(Properties props) {
        accessToken = props.getProperty(KEY_ACCESS_TOKEN);
        appId = props.getProperty(KEY_APP_ID);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppId() {
        return appId;
    }
}

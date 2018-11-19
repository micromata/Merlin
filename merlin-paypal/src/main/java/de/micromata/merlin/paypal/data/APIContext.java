package de.micromata.merlin.paypal.data;

import de.micromata.merlin.paypal.PayPalConfig;

public class APIContext {

    private String clientId, clientSecret;
    private String mode;

    public APIContext(PayPalConfig config) {
        this.clientId = config.getClientId();
        this.clientSecret = config.getClientSecret();
        this.mode = config.getMode().name().toLowerCase();
    }

    public String getMode() {
        return mode;
    }
}

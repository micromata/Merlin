package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.merlin.paypal.PayPalConfig;

public class RedirectUrls {
    private String returnUrl, cancelUrl;

    public RedirectUrls setConfig(PayPalConfig config) {
        this.returnUrl = config.getReturnUrl();
        this.cancelUrl = config.getCancelUrl();
        return this;
    }

    @JsonProperty(value = "return_url")
    public String getReturnUrl() {
        return returnUrl;
    }

    @JsonProperty(value = "cancel_url")
    public String getCancelUrl() {
        return cancelUrl;
    }
}

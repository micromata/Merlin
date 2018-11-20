package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.PayPalConfig;
import de.micromata.paypal.Utils;

public class RedirectUrls {
    private String returnUrl, cancelUrl;

    /**
     * Sets the return an cancel ur, if not already set. If one of the values is already set the set
     * value will not be overwritten.
     * @param config
     * @return
     */
    public RedirectUrls setConfig(PayPalConfig config) {
        if (Utils.isBlank(this.returnUrl))
            this.returnUrl = config.getReturnUrl();
        if (Utils.isBlank(this.cancelUrl))
            this.cancelUrl = config.getCancelUrl();
        return this;
    }

    @JsonProperty(value = "return_url")
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     * You may overwrite the default return url of {@link PayPalConfig}.
     * @param returnUrl
     * @return this for chaining.
     */
    public RedirectUrls setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        return this;
    }

    @JsonProperty(value = "cancel_url")
    public String getCancelUrl() {
        return cancelUrl;
    }

    /**
     * You may overwrite the default cancel url of {@link PayPalConfig}.
     * @param cancelUrl
     * @return this for chaining.
     */
    public RedirectUrls setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
        return this;
    }
}

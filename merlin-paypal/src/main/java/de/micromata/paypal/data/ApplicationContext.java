package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationContext {
    private String shippingPreference;

    @JsonProperty(value = "shipping_preference")
    public String getShippingPreference() {
        return shippingPreference;
    }

    public ApplicationContext setShippingPreference(ShippingPreference shippingPreference) {
        this.shippingPreference = shippingPreference.name();
        return this;
    }
}

package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.Utils;

public class PayerInfo {
    private String email, firstName, lastName, payerId, countryCode;
    private ShippingAddress shippingAddress;

    public String getEmail() {
        return email;
    }

    /**
     * Ensures maximum length of 127: https://developer.paypal.com/docs/api/payments/v1/#definition-payer_info
     * @param email
     */
    public void setEmail(String email) {
        this.email = Utils.ensureMaxLength(email, 127);
    }

    @JsonProperty(value = "first_name")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty(value = "last_name")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty(value = "payer_id")
    public String getPayerId() {
        return payerId;
    }

    @JsonProperty(value = "shipping_address")
    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }


    @JsonProperty(value = "country_code")
    public String getCountryCode() {
        return countryCode;
    }
}

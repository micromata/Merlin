package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.micromata.paypal.Utils;

public class ShippingAddress {
    private String recipientName, line1, line2, city, state, postalCode, countryCode;

    @JsonProperty(value = "recipient_name")
    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getLine1() {
        return line1;
    }

    /**
     * Ensures maximum length of 100: https://developer.paypal.com/docs/api/payments/v1/#definition-shipping_address
     *
     * @param line1
     */
    public void setLine1(String line1) {
        this.line1 = Utils.ensureMaxLength(line1, 100);
    }

    public String getLine2() {
        return line2;
    }

    /**
     * Ensures maximum length of 100: https://developer.paypal.com/docs/api/payments/v1/#definition-shipping_address
     *
     * @param line2
     */
    public void setLine2(String line2) {
        this.line2 = Utils.ensureMaxLength(line2, 100);
    }

    public String getCity() {
        return city;
    }

    /**
     * Ensures maximum length of 64: https://developer.paypal.com/docs/api/payments/v1/#definition-shipping_address
     *
     * @param city
     */
    public void setCity(String city) {
        this.city = Utils.ensureMaxLength(city, 64);
    }

    public String getState() {
        return state;
    }

    /**
     * Ensures maximum length of 40: https://developer.paypal.com/docs/api/payments/v1/#definition-shipping_address
     *
     * @param state
     */
    public void setState(String state) {
        this.state = Utils.ensureMaxLength(state, 40);
    }

    @JsonProperty(value = "postal_code")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JsonProperty(value = "country_code")
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * two-character ISO 3166-1 code: https://developer.paypal.com/docs/api/payments/v1/#definition-shipping_address.
     *
     * @param countryCode
     */
    public void setCountryCode(String countryCode) {
        if (countryCode.length() != 2) {
            throw new IllegalArgumentException("Country code must be a two-character ISO 3166-1 code: " + countryCode);
        }
        this.countryCode = countryCode;
    }
}

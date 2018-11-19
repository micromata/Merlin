package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShippingAddress {
    // "shipping_address": {
    //          "recipient_name": "test buyer",
    //          "line1": "ESpachstr. 1",
    //          "city": "Freiburg",
    //          "state": "Empty",
    //          "postal_code": "79111",
    //          "country_code": "DE"
    //        }
    private String recipientName, line1, city, state, postalCode, countryCode;

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

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}

package de.micromata.merlin.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayerInfo {
    //       "email": "...",
    //      "first_name": "test",
    //      "last_name": "buyer",
    //      "payer_id": "xxxx",
    //      "shipping_address": {
    //        "recipient_name": "test buyer",
    //        "line1": "ESpachstr. 1",
    //        "city": "Freiburg",
    //        "state": "Empty",
    //        "postal_code": "79111",
    //        "country_code": "DE"
    //      },
    //      "country_code": "DE"

    private String email, firstName, lastName, payerId, countryCode;
    private ShippingAddress shippingAddress;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

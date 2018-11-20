package de.micromata.paypal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ItemList {
    private List<Item> items = new ArrayList<>();
    private ShippingAddress shippingAddress;

    public List<Item> getItems() {
        return items;
    }

    public ItemList add(Item item) {
        items.add(item);
        return this;
    }

    @JsonProperty(value = "shipping_address")
    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }
}

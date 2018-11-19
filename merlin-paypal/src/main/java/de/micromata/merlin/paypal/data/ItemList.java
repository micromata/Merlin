package de.micromata.merlin.paypal.data;

import java.util.ArrayList;
import java.util.List;

public class ItemList {
    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public ItemList add(Item item) {
        items.add(item);
        return this;
    }
}

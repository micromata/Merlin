package de.reinhard.merlin.app.rest;

import java.util.LinkedList;
import java.util.List;

public class RestRegistry {
    private static final RestRegistry instance = new RestRegistry();
    private List<String> registeredServices = new LinkedList<>();

    public static RestRegistry getInstance() {
        return instance;
    }

    public String getServiceList() {
        return registeredServices.get(0); // Only 1 for the start.
    }

    private RestRegistry() {
        add(Configuration.class);
    }

    private void add(Class restServiceClass) {
        registeredServices.add(restServiceClass.getCanonicalName());
    }
}

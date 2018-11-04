package de.micromata.merlin.smarthome.examples.openhab_knx.data;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataStorage {
    private Logger log = LoggerFactory.getLogger(DataStorage.class);
    private static final DataStorage instance = new DataStorage();

    private Map<String, String> config = new HashMap<String, String>();
    private List<KnxThing> knxThings = new ArrayList<>();
    // Contains all knx things:
    private Map<String, KnxThing> knxThingRegistryMap = new HashMap<String, KnxThing>();
    private List<String> knxDevices = new ArrayList<String>();
    private List<String> knxPersistencies = new ArrayList<String>();
    // Contains all knx things as a list of things per device:
    private Map<String, List<KnxThing>> knxDeviceThingMap = new HashMap<String, List<KnxThing>>();
    private Map<String, List<KnxThing>> knxPersistencyThingMap = new HashMap<String, List<KnxThing>>();

    private DataStorage() {
    }

    public String getDateTime() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    public void add(KnxThing thing) {
        if (knxThingRegistryMap.containsKey(thing.getId()) == true) {
            log.warn("KNX thing added twice: id='" + thing.getId() + "``");
        }
        if (knxDevices.contains(thing.getDevice()) == false) {
            log.debug("Found KNX device: " + thing.getDevice());
            knxDevices.add(thing.getDevice());
            List<KnxThing> list = new ArrayList<KnxThing>();
            knxDeviceThingMap.put(thing.getDevice(), list);
        }
        List<KnxThing> list = knxDeviceThingMap.get(thing.getDevice());
        list.add(thing);
        if (StringUtils.isNotBlank(thing.getPersistency())) {
            if (knxPersistencies.contains(thing.getPersistency()) == false) {
                log.debug("Found KNX persistency: " + thing.getPersistency());
                knxPersistencies.add(thing.getPersistency());
                list = new ArrayList<KnxThing>();
                knxPersistencyThingMap.put(thing.getPersistency(), list);
            }
            list = knxPersistencyThingMap.get(thing.getPersistency());
            list.add(thing);
        }
        knxThings.add(thing);
    }

    public List<KnxThing> getKnxThings() {
        return knxThings;
    }

    public KnxThing getKnxThing(String id) {
        return knxThingRegistryMap.get(id);
    }

    public void setConfig(String key, String value) {
        config.put(key, value);
    }

    public String getConfig(String key) {
        return config.get(key);
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public List<String> getKnxDevices() {
        return knxDevices;
    }

    public Map<String, List<KnxThing>> getKnxDeviceThingMap() {
        return knxDeviceThingMap;
    }

    public List<String> getKnxPersistencies() {
        return knxPersistencies;
    }

    public Map<String, List<KnxThing>> getKnxPersistencyThingMap() {
        return knxPersistencyThingMap;
    }

    public static DataStorage getInstance() {
        return instance;
    }
}

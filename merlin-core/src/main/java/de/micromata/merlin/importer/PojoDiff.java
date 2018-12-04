package de.micromata.merlin.importer;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Represents a property change.
 */
public class PojoDiff {
    private static Logger log = LoggerFactory.getLogger(PojoDiff.class);

    public static List<PropertyDelta> getPropertyChanges(Object oldObject, Object newObject, String... fieldsToIgnore) {
        if (oldObject == null || newObject == null) {
            return null;
        }
        List<PropertyDelta> deltas = new ArrayList<>();
        PropertyDescriptor[] descs = PropertyUtils.getPropertyDescriptors(oldObject);
        for (PropertyDescriptor desc : descs) {
            String propertyName = desc.getName();
            if ("class".equals(propertyName)) continue;
            if (Arrays.asList(fieldsToIgnore).contains(propertyName)) { // check if field name is in ignoring list then skip that iteration. and continue to next.
                continue;
            }
            try {
                Object oldObjPropertyValue = PropertyUtils.getProperty(oldObject, propertyName); // value of property/field in oldObject.
                Object newObjPropertyValue = PropertyUtils.getProperty(newObject, propertyName); // value of property/field in newObject.
                if (oldObjPropertyValue == null && oldObjPropertyValue == null) { // if both objects are null then no need to do further steps just skip that iteration.
                    continue;
                } else if ((oldObjPropertyValue != null && newObjPropertyValue == null)
                        || (oldObjPropertyValue == null && newObjPropertyValue != null)) {
                    // if one of the object is null and another is not-null then it means there is a change then return false.
                } else if (oldObjPropertyValue.equals(newObjPropertyValue)) {
                    continue;
                }
                PropertyDelta delta = new PropertyDelta();
                delta.property = propertyName;
                delta.oldValue = oldObjPropertyValue;
                delta.newValue = newObjPropertyValue;
                deltas.add(delta);
                continue;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                log.error("Exception while comparing property '" + propertyName + "' of class '" + oldObject.getClass() + "': " + ex.getMessage());
            }
        }
        return deltas; // return final result.
    }

}

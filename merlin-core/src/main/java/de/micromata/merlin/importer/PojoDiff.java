package de.micromata.merlin.importer;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        BeanMap beanMap = new BeanMap(oldObject);
        PropertyUtilsBean propertyUtils = new PropertyUtilsBean();
        for (Object propertyNameObject : beanMap.keySet()) {
            String propertyName = (String) propertyNameObject;
            if (Arrays.asList(fieldsToIgnore).contains(propertyName)) { // check if field name is in ignoring list then skip that iteration. and continue to next.
                continue;
            }
            try {
                Object oldObjPropertyValue = propertyUtils.getProperty(oldObject, propertyName); // value of property/field in oldObject.
                Object newObjPropertyValue = propertyUtils.getProperty(newObject, propertyName); // value of property/field in newObject.
                if (oldObjPropertyValue == null && oldObjPropertyValue == null) { // if both objects are null then no need to do further steps just skip that iteration.
                    continue;
                } else if ((oldObjPropertyValue != null && newObjPropertyValue == null)
                        || (oldObjPropertyValue == null && newObjPropertyValue != null)) {
                    // if one of the object is null and another is not-null then it means there is a change then return false.
                } else if (oldObjPropertyValue.equals(newObjPropertyValue)) {
                    continue;
                }
                PropertyDelta delta = new PropertyDelta();
                delta.oldValue = oldObjPropertyValue;
                delta.newValue = newObjPropertyValue;
                deltas.add(delta);
                continue;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                log.error("Exception while comparing property '" + propertyName + "' of class '" + oldObject.getClass());
            }
        }
        return deltas; // return final result.
    }

    /**
     * Can be overridden by sub class to add additional property deltas.
     *
     * @return Collection of additional property deltas.
     */
    protected Collection<? extends PojoDiff> addAdditionalPropertyDeltas() {
        return Collections.emptyList();
    }

    protected Optional<PojoDiff> createPropertyDelta(String fieldname, Object newObject, Object
            origValue, Class<?> type) {/*
    boolean modified = false;
    if (type == BigDecimal.class) {
      if (NumberHelper.isEqual((BigDecimal) newValue, (BigDecimal) origValue) == false) {
        modified = true;
      }
    } else if (ObjectUtils.equals(newValue, origValue) == false) {
      modified = true;
    }
    if (modified) {
      Object ov;
      Object nv;
      if (origValue instanceof ShortDisplayNameCapable) {
        ov = ((ShortDisplayNameCapable) origValue).getShortDisplayName();
      } else {
        ov = origValue;
      }
      if (newValue instanceof ShortDisplayNameCapable) {
        nv = ((ShortDisplayNameCapable) newValue).getShortDisplayName();
      } else {
        nv = newValue;
      }
      return Optional.of(new MySimplePropertyDelta(fieldname, type, ov, nv));
    }
*/
        return Optional.empty();
    }
}

package de.micromata.merlin.importer

import org.apache.commons.beanutils.PropertyUtils
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Represents a property change.
 */
object PojoDiff {
    private val log = LoggerFactory.getLogger(PojoDiff::class.java)
    @JvmStatic
    fun getPropertyChanges(oldObject: Any?, newObject: Any?, vararg fieldsToIgnore: String?): List<PropertyDelta>? {
        if (oldObject == null || newObject == null) {
            return null
        }
        val deltas: MutableList<PropertyDelta> = ArrayList()
        val descs = PropertyUtils.getPropertyDescriptors(oldObject)
        for (desc in descs) {
            val propertyName = desc.name
            if ("class" == propertyName) continue
            if (Arrays.asList<String>(*fieldsToIgnore).contains(propertyName)) { // check if field name is in ignoring list then skip that iteration. and continue to next.
                continue
            }
            try {
                val oldObjPropertyValue = PropertyUtils.getProperty(oldObject, propertyName) // value of property/field in oldObject.
                val newObjPropertyValue = PropertyUtils.getProperty(newObject, propertyName) // value of property/field in newObject.
                if (oldObjPropertyValue == null && oldObjPropertyValue == null) { // if both objects are null then no need to do further steps just skip that iteration.
                    continue
                } else if (oldObjPropertyValue != null && newObjPropertyValue == null
                        || oldObjPropertyValue == null && newObjPropertyValue != null) { // if one of the object is null and another is not-null then it means there is a change then return false.
                } else if (oldObjPropertyValue == newObjPropertyValue) {
                    continue
                }
                val delta = PropertyDelta()
                delta.property = propertyName
                delta.oldValue = oldObjPropertyValue
                delta.newValue = newObjPropertyValue
                deltas.add(delta)
                continue
            } catch (ex: IllegalAccessException) {
                log.error("Exception while comparing property '" + propertyName + "' of class '" + oldObject.javaClass + "': " + ex.message)
            } catch (ex: InvocationTargetException) {
                log.error("Exception while comparing property '" + propertyName + "' of class '" + oldObject.javaClass + "': " + ex.message)
            } catch (ex: NoSuchMethodException) {
                log.error("Exception while comparing property '" + propertyName + "' of class '" + oldObject.javaClass + "': " + ex.message)
            }
        }
        return deltas // return final result.
    }
}

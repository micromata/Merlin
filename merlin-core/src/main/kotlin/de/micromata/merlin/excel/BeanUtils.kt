package de.micromata.merlin.excel

import org.apache.commons.lang3.ArrayUtils
import java.lang.reflect.Field

object BeanUtils {
    fun getValue(src: Any, property: String): Any? {
        val srcClazz = src.javaClass
        val srcField = getDeclaredField(srcClazz, property) ?: return null
        srcField.isAccessible = true
        return srcField.get(src)
    }

    /**
     * Return all fields declared by the given class and all super classes.
     *
     * @param clazz
     * @return
     * @see Class.getDeclaredFields
     */
    private fun getAllDeclaredFields(clazz: Class<*>): Array<Field> {
        var currentClazz = clazz
        var fields = currentClazz.declaredFields
        while (currentClazz.superclass != null) {
            currentClazz = currentClazz.superclass
            fields = ArrayUtils.addAll(fields, *currentClazz.declaredFields) as Array<Field>
        }
        return fields
    }

    /**
     * Return the matching field declared by the given class or any super class.
     *
     * @param clazz
     * @param fieldnames
     * @return the field or null
     */
    private fun getDeclaredField(clazz: Class<*>, fieldname: String): Field? {
        val fields: Array<Field> = getAllDeclaredFields(clazz)
        for (f in fields) {
            if (f.name == fieldname) {
                return f
            }
        }
        return null
    }
}

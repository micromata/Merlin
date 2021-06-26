package de.micromata.merlin.excel

import org.apache.commons.lang3.ArrayUtils
import java.lang.reflect.Field
import java.lang.reflect.Method

object BeanUtils {
    fun getValue(src: Any, property: String): Any? {
        val srcClazz = src.javaClass
        val srcField = getDeclaredField(srcClazz, property) ?: return null
        return getValue(src, srcField)
    }

    fun getValue(src: Any, field: Field): Any? {
        field.isAccessible = true
        return field.get(src)
    }

    fun getValue(src: Any, getter: Method): Any? {
        getter.isAccessible = true
        return getter.invoke(src)
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
     * Return all fields declared by the given class and all super classes.
     *
     * @param clazz
     * @return
     */
    private fun getAllDeclaredGetters(clazz: Class<*>): Array<Method> {
        var currentClazz = clazz
        var methods = currentClazz.declaredMethods
        while (currentClazz.superclass != null) {
            currentClazz = currentClazz.superclass
            methods = ArrayUtils.addAll(methods, *currentClazz.declaredMethods) as Array<Method>
        }
        return methods
    }

    /**
     * Return the matching field declared by the given class or any super class.
     *
     * @param clazz
     * @param fieldname
     * @return the field or null
     */
    fun getDeclaredField(clazz: Class<*>, fieldname: String): Field? {
        val fields: Array<Field> = getAllDeclaredFields(clazz)
        for (f in fields) {
            if (f.name == fieldname) {
                return f
            }
        }
        return null
    }

    /**
     * Return the matching field declared by the given class or any super class.
     *
     * @param clazz
     * @param fieldname
     * @return the field or null
     */
    fun getGetterMethod(clazz: Class<*>, fieldname: String): Method? {
        val methods: Array<Method> = getAllDeclaredGetters(clazz)
        for (m in methods) {
            if (m.name == "get${fieldname.replaceFirstChar { it.uppercase() }}") {
                return m
            }
        }
        return null
    }
}

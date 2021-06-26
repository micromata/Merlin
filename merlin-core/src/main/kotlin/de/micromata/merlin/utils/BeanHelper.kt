/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2014 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.merlin.utils

import de.micromata.merlin.excel.importer.ImportLogger
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Stores one imported object (e. g. MS Excel row as bean object). It also contains information about the status: New object or modified
 * object.
 *
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
object BeanHelper {
    @JvmStatic
    @JvmOverloads
    fun determineGetter(clazz: Class<*>, fieldname: String, onlyPublicGetter: Boolean = true): Method? {
        val cap = fieldname.replaceFirstChar { it.uppercase() }
        val methods: Array<Method> = getAllDeclaredMethods(clazz) ?: return null
        for (method in methods) {
            if (onlyPublicGetter && !Modifier.isPublic(method.modifiers)) {
                continue
            }
            val matches =
                if (Boolean::class.javaPrimitiveType!!.isAssignableFrom(method.returnType)) {
                    "is$cap" == method.name || "has$cap" == method.name || "get$cap" == method.name
                } else {
                    "get$cap" == method.name
                }
            if (matches) {
                if (!method.isBridge) { // Don't return bridged methods (methods defined in interface or super class with different return type).
                    return method
                }
            }
        }
        return null

    }

    @JvmStatic
    fun getAllDeclaredMethods(clazz: Class<*>): Array<Method>? {
        var cls = clazz
        var methods = cls.declaredMethods
        while (cls.superclass != null) {
            cls = cls.superclass
            methods += cls.declaredMethods
        }
        return methods
    }

    /**
     * Does not work for multiple setter methods with one argument and different parameter type (e. g. setField(Date) and
     * setField(long)).
     *
     * @param clazz
     * @param fieldname
     * @return
     */
    @JvmStatic
    fun determineSetter(clazz: Class<*>, fieldname: String): Method? {
        val cap = fieldname.replaceFirstChar { it.uppercaseChar() }
        val methods: Array<Method> = getAllDeclaredMethods(clazz) ?: return null
        for (method in methods) {
            if ("set$cap" == method.name && method.parameterTypes.size == 1) {
                return method
            }
        }
        return null
    }

    @JvmStatic
    operator fun invoke(obj: Any, method: Method, vararg args: Any?): Any? {
        return try {
            method.invoke(obj, *args)
        } catch (ex: Exception) {
            val argsString = args.joinToString(", ")
            log.error("Could not invoke '${method.name}': ${ex.message} for object [$obj] with args: $argsString", ex)
            throw RuntimeException(ex)
        }
    }

    /**
     * Sets the property of the given bean, if a setter method is found and the given value is assignable to the bean variable.
     */
    @JvmStatic
    fun setProperty(bean: Any, property: String, value: Any?, ignoreNullValue: Boolean = false) {
        if (ignoreNullValue && value == null)
            return
        val setter = determineSetter(bean::class.java, property) ?: return
        val type = setter.parameterTypes[0]
        setProperty(bean, setter, type, value, ignoreNullValue)
    }

    /**
     * Sets the property of the given bean, if a setter method is found and the given value is assignable to the bean variable.
     */
    @JvmStatic
    fun setProperty(bean: Any, setter: Method, type: Class<*>, value: Any?, ignoreNullValue: Boolean = false) {
        if (ignoreNullValue && value == null)
            return
        if (value != null) {
            val valueType = value::class.java
            if (type.isAssignableFrom(valueType)) {
                invoke(bean, setter, value)
            } else if (value is Number) {
                if (type == java.lang.Integer::class.java || type == Integer::class.java) {
                    invoke(bean, setter, value.toInt())
                } else if (type == java.lang.Double::class.java || type == Double::class.java) {
                    invoke(bean, setter, value.toDouble())
                } else if (type == java.lang.Float::class.java || type == Float::class.java) {
                    invoke(bean, setter, value.toFloat())
                } else if (type == BigDecimal::class.java) {
                    invoke(bean, setter, BigDecimal(value.toString()))
                } else if (type == BigInteger::class.java) {
                    invoke(bean, setter, BigInteger(value.toString()))
                }
            } else if (value is LocalDateTime) {
                if (type.isAssignableFrom(LocalDate::class.java)) {
                    invoke(bean, setter, value.toLocalDate())
                }
            }
        } else {
            invoke(bean, setter, null)
        }
    }

    private val log = LoggerFactory.getLogger(ImportLogger::class.java)
}

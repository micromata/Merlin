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
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Stores one imported object (e. g. MS Excel row as bean object). It also contains information about the status: New object or modified
 * object.
 *
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
object BeanHelper {
    //private val log = LoggerFactory.getLogger(BeanHelper::class.java)

    @JvmStatic
    @JvmOverloads
    fun determineGetter(clazz: Class<*>, fieldname: String, onlyPublicGetter: Boolean = true): Method? {
        val cap = StringUtils.capitalize(fieldname)
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
    @JvmOverloads
    fun getAllDeclaredMethods(clazz: Class<*>): Array<Method>? {
        var cls = clazz
        var methods = cls.declaredMethods
        while (cls.superclass != null) {
            cls = cls.superclass
            methods = ArrayUtils.addAll(methods, *cls.declaredMethods) as Array<Method>
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
        val cap = fieldname.capitalize()
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
        val setter = BeanHelper.determineSetter(bean::class.java, property) ?: return
        if (value != null) {
            val valueType = value::class.java
            val type = setter.parameterTypes[0]
            if (type.isAssignableFrom(valueType)) {
                invoke(bean, setter, value)
            }
        } else {
            invoke(bean, setter, null)
        }
    }

    private val log = LoggerFactory.getLogger(ImportLogger::class.java)
}

package de.micromata.merlin.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class BeanHelperTest {
    open class A(var number: Int? = null,
                 var name: String? = null,
                 var enabled: Boolean = false)

    class B(var street: String? = null) : A()

    @Test
    fun determineTest() {
        var method = BeanHelper.determineGetter(A::class.java, "number")
        Assertions.assertEquals("getNumber", method!!.name)
        method = BeanHelper.determineSetter(A::class.java, "number")
        Assertions.assertEquals("setNumber", method!!.name)

        method = BeanHelper.determineSetter(A::class.java, "unknown")
        Assertions.assertNull(method)
    }

    @Test
    fun invokeTest() {
        val objA = A()
        BeanHelper.setProperty(objA, "name", "Test")
        Assertions.assertEquals("Test", objA.name)
        BeanHelper.setProperty(objA, "number", 42)
        Assertions.assertEquals(42, objA.number)

        val objB = B()
        BeanHelper.setProperty(objB, "name", "Test")
        Assertions.assertEquals("Test", objB.name)
        BeanHelper.setProperty(objB, "number", 42)
        Assertions.assertEquals(42, objB.number)
        BeanHelper.setProperty(objB, "street", "Street")
        Assertions.assertEquals("Street", objB.street)

        BeanHelper.setProperty(objB, "street", null, true)
        Assertions.assertEquals("Street", objB.street)
        BeanHelper.setProperty(objB, "street", null)
        Assertions.assertNull(objB.street)
    }
}


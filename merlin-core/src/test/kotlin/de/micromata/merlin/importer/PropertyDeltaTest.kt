package de.micromata.merlin.importer

import de.micromata.merlin.excel.importer.ImportStorage
import de.micromata.merlin.excel.importer.ImportedElement
import de.micromata.merlin.excel.importer.ImportedSheet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class PropertyDeltaTest {
    class TestData(val str: String?, val decimalValue: BigDecimal?, val intValue: Int?)

    @Test
    fun createPropertyDeltaTest() {
        val importedSheet = ImportedSheet<TestData>(ImportStorage())
        val element = ImportedElement<TestData>(importedSheet, TestData::class.java, "str", "decimalValue", "intValue")
        element.value = TestData(null, null, null)
        element.oldValue = TestData(null, null, null)
        Assertions.assertTrue(element.propertyChanges.isNullOrEmpty())
        element.value = TestData("Test", BigDecimal.ONE, 1)
        Assertions.assertEquals(3, element.propertyChanges!!.size)
        Assertions.assertNull(element.propertyChanges!![0].oldValue)
        Assertions.assertEquals("Test", element.propertyChanges!![0].newValue)
        Assertions.assertNull(element.propertyChanges!![1].oldValue)
        Assertions.assertEquals("1", element.propertyChanges!![1].newValue)
        Assertions.assertNull(element.propertyChanges!![2].oldValue)
        Assertions.assertEquals("1", element.propertyChanges!![2].newValue)
        element.oldValue = TestData("Test", BigDecimal.ONE.setScale(5), 1)
        Assertions.assertTrue(element.propertyChanges.isNullOrEmpty())
    }
}

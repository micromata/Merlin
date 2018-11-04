package de.micromata.merlin.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterTest {
    @Test
    public void formatNumberTest() {
        assertEquals("0", Converter.formatNumber(0, 5));
        assertEquals("-5", Converter.formatNumber(-5, 5));
        assertEquals("9", Converter.formatNumber(9, 9));
        assertEquals("00", Converter.formatNumber(0, 10));
        assertEquals("09", Converter.formatNumber(9, 10));
        assertEquals("42", Converter.formatNumber(42, 99));
        assertEquals("000", Converter.formatNumber(0, 100));
        assertEquals("009", Converter.formatNumber(9, 100));
        assertEquals("042", Converter.formatNumber(42, 100));
        assertEquals("0042", Converter.formatNumber(42, 1000));
        assertEquals("1000", Converter.formatNumber(1000, 1000));
        assertEquals("0000", Converter.formatNumber(0, 1000));
    }
}

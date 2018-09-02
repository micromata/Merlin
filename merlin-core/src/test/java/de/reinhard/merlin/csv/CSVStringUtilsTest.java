package de.reinhard.merlin.csv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CSVStringUtilsTest {
    @Test
    public void parseStringList() {
        assertArrayEquals(new String[0], CSVStringUtils.parseStringList(null));
        assertArrayEquals(new String[0], CSVStringUtils.parseStringList(""));
        assertArrayEquals(new String[]{"Berta"}, CSVStringUtils.parseStringList("Berta"));
        assertArrayEquals(new String[]{"Berta"}, CSVStringUtils.parseStringList("„Berta“"));
        assertArrayEquals(new String[]{"Berta", "Horst"}, CSVStringUtils.parseStringList("„Berta“, \"Horst\""));
        assertArrayEquals(new String[]{"Berta", "Horst"}, CSVStringUtils.parseStringList("Berta, Horst"));
        assertArrayEquals(new String[]{"Berta's", "Horst"}, CSVStringUtils.parseStringList("„Berta's“, „Horst"));
        assertArrayEquals(new String[]{"A", "B", "", "D"}, CSVStringUtils.parseStringList("„A“, „B\", '', D"));
        assertArrayEquals(new String[]{"A", "B", "", "D"}, CSVStringUtils.parseStringList("„A“, „B\"  '' D"));
        assertArrayEquals(new String[]{"A', „B", "", "D"}, CSVStringUtils.parseStringList("„A', „B\"  '' D"));
    }
}

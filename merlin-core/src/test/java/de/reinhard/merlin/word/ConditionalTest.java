package de.reinhard.merlin.word;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ConditionalTest {
    private Logger log = LoggerFactory.getLogger(ConditionalTest.class);

    @Test
    public void parseStringList() {
        assertArrayEquals(new String[0], Conditional.parseStringList(null));
        assertArrayEquals(new String[0], Conditional.parseStringList(""));
        assertArrayEquals(new String[]{"Berta"}, Conditional.parseStringList("Berta"));
        assertArrayEquals(new String[]{"Berta"}, Conditional.parseStringList("„Berta“"));
        assertArrayEquals(new String[]{"Berta", "Horst"}, Conditional.parseStringList("„Berta“, \"Horst\""));
        assertArrayEquals(new String[]{"Berta", "Horst"}, Conditional.parseStringList("Berta, Horst"));
        assertArrayEquals(new String[]{"Berta's", "Horst"}, Conditional.parseStringList("„Berta's“, „Horst"));
        assertArrayEquals(new String[]{"A", "B", "", "D"}, Conditional.parseStringList("„A“, „B\", '', D"));
        assertArrayEquals(new String[]{"A", "B", "", "D"}, Conditional.parseStringList("„A“, „B\"  '' D"));
        assertArrayEquals(new String[]{"A', „B", "", "D"}, Conditional.parseStringList("„A', „B\"  '' D"));
    }
}

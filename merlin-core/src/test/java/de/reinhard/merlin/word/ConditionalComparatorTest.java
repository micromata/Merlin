package de.reinhard.merlin.word;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConditionalComparatorTest {
    private Logger log = LoggerFactory.getLogger(ConditionalComparatorTest.class);

    @Test
    public void greaterThan() {
        assertTrue(ConditionalComparator.greaterThan(5, 4));
        assertTrue(ConditionalComparator.greaterThan(4.0001, 4));
        assertFalse(ConditionalComparator.greaterThan(4.0000001, 4));
    }

    @Test
    public void equals() {
        assertTrue(ConditionalComparator.equals(4.0000001, 3.9999999));
        assertFalse(ConditionalComparator.equals(4 + ConditionalComparator.EPSILON, 4 - ConditionalComparator.EPSILON));
    }
}
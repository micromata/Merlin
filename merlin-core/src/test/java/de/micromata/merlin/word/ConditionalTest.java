package de.micromata.merlin.word;

import de.micromata.merlin.csv.CSVStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionalTest {
    private Logger log = LoggerFactory.getLogger(ConditionalTest.class);

    @Test
    void regexpTest() {
        assertMatcher("{if Arbeitszeit = „Teilzeit“}...", "Arbeitszeit", "=", "Teilzeit");
        assertMatcher("{if Arbeitszeit = ‚Teilzeit‘}...", "Arbeitszeit", "=", "Teilzeit");
        assertMatcher("{if Arbeitszeit != ‚Vollzeit‘}...", "Arbeitszeit", "!=", "Vollzeit");
        assertMatcher("{if Arbeitszeit in ‚Vollzeit‘}...", "Arbeitszeit", "in", "Vollzeit");
        assertMatcher("{if Arbeitszeit !in ‚Vollzeit‘}...", "Arbeitszeit", "!in", "Vollzeit");
        assertMatcher("{if Arbeitszeit !in ‚Vollzeit\"}...", "Arbeitszeit", "!in", "Vollzeit\"");
        assertMatcher("{if name = „Horst's“}", "name", "=", "Horst's");
    }

    private void assertMatcher(String str, String... groups) {
        Matcher matcher = AbstractConditional.beginIfPattern.matcher(str);
        assertEquals(groups.length > 0 ? true : false, matcher.find());
        if (groups.length == 0) {
            return;
        }
        assertEquals(4, matcher.groupCount(), "Number of regexp group count.");
        assertEquals(groups[0], matcher.group(2));
        assertEquals(groups[1], matcher.group(3));
        String[] params = CSVStringUtils.parseStringList(matcher.group(3));
        assertEquals(groups.length - 2, params.length, "Number of comma separated values.");
        for (int i = 3; i < groups.length; i++) {
            assertEquals(groups[i], params[i - 3]);
        }
    }

}

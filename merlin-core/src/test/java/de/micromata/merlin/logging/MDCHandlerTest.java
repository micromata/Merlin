package de.micromata.merlin.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MDCHandlerTest {
    @Test
    public void putRestoreTest() {
        MDCHandler mdc1 = new MDCHandler();
        mdc1.put(MDCKey.TEMPLATE_PK, "t1");
        mdc1.put(MDCKey.TEMPLATE_DEFINITION_PK, "td1");
        assertEquals("t1", MDC.get(MDCKey.TEMPLATE_PK.mdcKey()));
        assertEquals("td1", MDC.get(MDCKey.TEMPLATE_DEFINITION_PK.mdcKey()));

        MDCHandler mdc2 = new MDCHandler();
        mdc2.put(MDCKey.TEMPLATE_PK, "t2");
        mdc2.put(null, "Test");
        assertEquals("t2", MDC.get(MDCKey.TEMPLATE_PK.mdcKey()));
        assertEquals("td1", MDC.get(MDCKey.TEMPLATE_DEFINITION_PK.mdcKey()));
        mdc2.restore();

        assertEquals("t1", MDC.get(MDCKey.TEMPLATE_PK.mdcKey()));
        assertEquals("td1", MDC.get(MDCKey.TEMPLATE_DEFINITION_PK.mdcKey()));

        mdc1.restore();

        assertNull(MDC.get(MDCKey.TEMPLATE_PK.mdcKey()));
        assertNull(MDC.get(MDCKey.TEMPLATE_DEFINITION_PK.mdcKey()));
    }
}

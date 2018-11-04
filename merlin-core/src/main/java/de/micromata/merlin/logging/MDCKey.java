package de.micromata.merlin.logging;

public enum MDCKey {
    TEMPLATE_PK("t.pk"), TEMPLATE_DEFINITION_PK("td.pk");

    private String mdcKey;

    MDCKey(String mdcKey) {
        this.mdcKey = mdcKey;
    }

    public String mdcKey() {
        return mdcKey;
    }
}

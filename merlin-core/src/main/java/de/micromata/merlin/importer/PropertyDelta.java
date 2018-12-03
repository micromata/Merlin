package de.micromata.merlin.importer;

import lombok.Getter;

/**
 * Represents a property change.
 */
public class PropertyDelta {
    @Getter
    String property;
    @Getter
    Object oldValue;
    @Getter
    Object newValue;
}

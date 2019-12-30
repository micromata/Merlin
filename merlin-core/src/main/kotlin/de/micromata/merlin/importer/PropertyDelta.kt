package de.micromata.merlin.importer

/**
 * Represents a property change.
 */
class PropertyDelta(var property: String? = null,
                    var oldValue: Any? = null,
                    var newValue: Any? = null)

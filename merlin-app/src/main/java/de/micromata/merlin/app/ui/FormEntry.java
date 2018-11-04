package de.reinhard.merlin.app.ui;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract base class.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FormLabelField.class, name = "field"),
        @JsonSubTypes.Type(value = FormContainer.class, name = "container")
})
public abstract class FormEntry {
}

package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.reinhard.merlin.word.templating.TemplateDefinition;

public interface JsonIdGenerator {
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "refId", scope = TemplateDefinition.class)
    public interface TemplateDefinitionJson {
    }
}

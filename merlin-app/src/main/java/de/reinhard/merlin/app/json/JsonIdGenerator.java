package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.reinhard.merlin.word.templating.VariableDefinition;

public interface JsonIdGenerator {
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id", scope = VariableDefinition.class)
    public interface VariableDefinitionJson {
    }

    ;
}

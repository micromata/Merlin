package de.reinhard.merlin.app.json;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.reinhard.merlin.word.ConditionalComparator;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.VariableDefinition;

public interface JsonIdGenerator {
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "refId", scope = VariableDefinition.class)
    public interface VariableDefinitionJson {
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "refId", scope = ConditionalComparator.class)
    public interface ConditionalComparatorJson {
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "refId", scope = TemplateDefinition.class)
    public interface TemplateDefinitionJson {
    }
}

package de.reinhard.merlin.app.json;

import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.VariableDefinition;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonUtilsTest {
    private Logger log = LoggerFactory.getLogger(JsonUtilsTest.class);

    @Test
    public void toJsonTest() throws IOException {
        TemplateDefinition t1 = new TemplateDefinition();
        t1.setFilenamePattern("Contract");
        assertEquals("{\"variableDefinitions\":[],\"filenamePattern\":\"Contract\"}", JsonUtils.toJson(t1));
        t1.add(createVar("Name", true, true));
        t1.add(createVar("Sex", true, false));
        String json = JsonUtils.toJson(t1);
        TemplateDefinition t2 = JsonUtils.fromJson(TemplateDefinition.class, json);
        assertEquals(t1.getFilenamePattern(), t2.getFilenamePattern());
        assertEquals(t1.getVariableDefinitions().size(), t2.getVariableDefinitions().size());
        for (int i = 0; i < t1.getVariableDefinitions().size(); i++) {
            assertVar(t1.getVariableDefinitions().get(i), t2.getVariableDefinitions().get(i));
        }
    }

    private VariableDefinition createVar(String name, boolean required, boolean unique) {
        return new VariableDefinition(name)
                .setDescription("This is a description.")
                .setRequired(required).setUnique(unique);
    }

    private void assertVar(VariableDefinition v1, VariableDefinition v2) {
        assertEquals(v1.getVariableName(), v2.getVariableName());
        assertEquals(v1.getDescription(), v2.getDescription());
        assertEquals(v1.isRequired(), v2.isRequired());
        assertEquals(v1.isUnique(), v2.isUnique());
    }
}
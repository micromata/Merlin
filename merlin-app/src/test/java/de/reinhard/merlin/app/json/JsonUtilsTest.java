package de.reinhard.merlin.app.json;

import de.reinhard.merlin.app.storage.Storage;
import de.reinhard.merlin.app.storage.TestData;
import de.reinhard.merlin.word.templating.DependentVariableDefinition;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.VariableDefinition;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonUtilsTest {
    private Logger log = LoggerFactory.getLogger(JsonUtilsTest.class);

    @Test
    public void toJsonTest() {
        TemplateDefinition t1 = new TemplateDefinition();
        t1.setName("Test template").setDescription("This is a test.").setFilenamePattern("Contract");
        assertEquals("{\"variableDefinitions\":[],\"dependentVariableDefinitions\":[],\"id\":\"" + t1.getId()
                + "\",\"name\":\"Test template\",\"description\":\"This is a test.\",\"filenamePattern\":\"Contract\"}", JsonUtils.toJson(t1));
        assertEquals(20, t1.getId().length());
        t1.add(createVar("Name", true, true));
        t1.add(createVar("Sex", true, false));
        String json = JsonUtils.toJson(t1);
        TemplateDefinition t2 = JsonUtils.fromJson(TemplateDefinition.class, json);
        assertTemp(t1, t2);
    }

    @Test
    public void testData() {
        TestData.create();
        List<TemplateDefinition> ts1 = Storage.getInstance().getTemplatesList();
        String json = JsonUtils.toJson(ts1);
        log.debug(json);
        TemplateDefinition t1 = ts1.get(0);
        TemplateDefinition t2 = JsonUtils.fromJson(TemplateDefinition.class, JsonUtils.toJson(t1));
        assertTemp(t1, t2);
        log.info(json);
    }

    private VariableDefinition createVar(String name, boolean required, boolean unique) {
        return new VariableDefinition(name)
                .setDescription("This is a description.")
                .setRequired(required).setUnique(unique);
    }

    private void assertTemp(TemplateDefinition t1, TemplateDefinition t2) {
        assertEquals(t1.getName(), t2.getName());
        assertEquals(t1.getDescription(), t2.getDescription());
        assertEquals(t1.getFilenamePattern(), t2.getFilenamePattern());
        assertEquals(t1.getVariableDefinitions().size(), t2.getVariableDefinitions().size());
        for (int i = 0; i < t1.getVariableDefinitions().size(); i++) {
            assertVar(t1.getVariableDefinitions().get(i), t2.getVariableDefinitions().get(i));
        }
        assertEquals(t1.getDependentVariableDefinitions().size(), t2.getDependentVariableDefinitions().size());
        for (int i = 0; i < t1.getDependentVariableDefinitions().size(); i++) {
            assertDepVar(t1.getDependentVariableDefinitions().get(i), t2.getDependentVariableDefinitions().get(i));
        }
    }

    private void assertVar(VariableDefinition v1, VariableDefinition v2) {
        assertEquals(v1.getName(), v2.getName());
        assertEquals(v1.getDescription(), v2.getDescription());
        assertEquals(v1.isRequired(), v2.isRequired());
        assertEquals(v1.isUnique(), v2.isUnique());
    }

    private void assertDepVar(DependentVariableDefinition v1, DependentVariableDefinition v2) {
        assertEquals(v1.getName(), v2.getName());
        assertEquals(v1.getDependsOn().getName(), v2.getDependsOn().getName());
        assertEquals(v1.getMapping().size(), v2.getMapping().size());
        for (Object entry : v1.getMapping().entrySet()) {
            assertEquals(v1.getMapping().get(entry), v2.getMapping().get(entry));
        }
    }
}
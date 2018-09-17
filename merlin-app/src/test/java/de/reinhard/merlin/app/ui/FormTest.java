package de.reinhard.merlin.app.ui;

import de.reinhard.merlin.app.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FormTest {
    private Logger log = LoggerFactory.getLogger(FormTest.class);

    @Test
    public void toJsonTest() {
        Form form1 = new Form();
        form1.add(new FormLabelField("name", "Name").setRequired(true));
        form1.add(new FormLabelField("age", "Age").setRequired(true).setValueType(FormLabelFieldValueType.INTEGER).setMinumumValue(0).setMaximumValue(120));
        form1.add(new FormLabelField("remark", "Remark"));
        form1.add(new FormLabelField("language", "Language").addOption("en", "English").addOption("de", "Deutsch"));
        form1.add(new FormLabelField("directory", "Directory").setValueType(FormLabelFieldValueType.DIRECTORY));
        String json = JsonUtils.toJson(form1);
        Form form2 = JsonUtils.fromJson(Form.class, json);
        assertForm(form1, form2);
    }

    private void assertForm(Form form1, Form form2) {
        assertEquals(form1.getEntries().size(), form2.getEntries().size());
        for (int i = 0; i < form1.getEntries().size(); i++) {
            if (form1.getEntries().get(i) instanceof FormLabelField) {
                FormLabelField f1 = (FormLabelField) form1.getEntries().get(i);
                FormLabelField f2 = (FormLabelField) form2.getEntries().get(i);
                assertEquals(f1.getPath(), f2.getPath());
                assertEquals(f1.getLabel(), f2.getLabel());
                assertEquals(f1.isRequired(), f2.isRequired());
                assertEquals(f1.getValueType(), f2.getValueType());
                assertEquals(f1.getMinumumValue(), f2.getMinumumValue());
                assertEquals(f1.getMaximumValue(), f2.getMaximumValue());
                if (f1.getOptions() == null) {
                    assertNull(f2.getOptions());
                } else {
                    assertEquals(f1.getOptions().size(), f2.getOptions().size());
                    for (int j = 0; j < f1.getOptions().size(); j++) {
                        FormLabelFieldOption o1 = f1.getOptions().get(j);
                        FormLabelFieldOption o2 = f2.getOptions().get(j);
                        assertEquals(o1.getLabel(), o2.getLabel());
                        assertEquals(o1.getValue(), o2.getValue());
                    }
                }
            }
        }
    }
}
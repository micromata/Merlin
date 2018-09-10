package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.Definitions;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DefinitionExcelConverterTest {
    private Logger log = LoggerFactory.getLogger(DefinitionExcelConverterTest.class);

    @Test
    public void createExcelTest() throws IOException {
        ExcelWorkbook workbook = DefinitionExcelConverter.writeToWorkbook(create());
        File file = new File(Definitions.OUTPUT_DIR, "TemplateDefinition.xlsx");
        log.info("Writing modified Excel file: " + file.getAbsolutePath());
        workbook.getPOIWorkbook().write(new FileOutputStream(file));
    }

    @Test
    public void getBooleanAsStringTest() {
        assertEquals("X", DefinitionExcelConverter.getBooleanAsString(true));
        assertEquals("", DefinitionExcelConverter.getBooleanAsString(false));
    }

    @Test
    public void getStringAsBooleanTest() {
        assertFalse(DefinitionExcelConverter.getStringAsBoolean(null));
        assertFalse(DefinitionExcelConverter.getStringAsBoolean(""));
        assertFalse(DefinitionExcelConverter.getStringAsBoolean("no"));
        assertFalse(DefinitionExcelConverter.getStringAsBoolean("-"));
        assertFalse(DefinitionExcelConverter.getStringAsBoolean("sdlfkje9"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("X"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("x"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("Y"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("y"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("yes"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("Yes"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("J"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("j"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("Ja"));
        assertTrue(DefinitionExcelConverter.getStringAsBoolean("ja"));
    }

    private TemplateDefinition create() {
        TemplateDefinition template = new TemplateDefinition();
        template.setName("Employment contract").setFilenamePattern("employment-contract-${Employee}").setDescription("This template is used for the generation of emloyee contracts.");
        VariableDefinition sex = createStringVariable("Sex", "Sex of the employee.", true, true).addAllowedValues("male", "female");
        template.add(createStringVariable("Employee", "Name of the employee.", true, true));
        template.add(new VariableDefinition(VariableType.DATE, "BeginDate").setDescription("Begin of the contract.").setRequired());
        template.add(sex);
        template.add(new VariableDefinition(VariableType.INT, "WeeklyHours").setDescription("The weekly working hours.").setRequired().setMinimumValue(1).setMaximumValue(40));
        template.add(new DependentVariableDefinition().setName("Mr_Mrs").setDependsOn(sex).addMapping("male", "Mr.").addMapping("female", "Mrs."));
        template.add(new DependentVariableDefinition().setName("He_She").setDependsOn(sex).addMapping("male", "He").addMapping("female", "She"));
        template.add(new DependentVariableDefinition().setName("he_she").setDependsOn(sex).addMapping("male", "he").addMapping("female", "she"));
        template.add(new DependentVariableDefinition().setName("His_Her").setDependsOn(sex).addMapping("male", "His").addMapping("female", "Her"));
        template.add(new DependentVariableDefinition().setName("his_her").setDependsOn(sex).addMapping("male", "his").addMapping("female", "her"));
        return template;
    }

    private VariableDefinition createStringVariable(String name, String description, boolean required, boolean unique) {
        return new VariableDefinition(name)
                .setDescription(description)
                .setRequired(required).setUnique(unique);
    }

}

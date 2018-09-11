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
        TemplateDefinitionExcelWriter writer = new TemplateDefinitionExcelWriter();
        TemplateDefinition originalTemplate = create();
        ExcelWorkbook workbook = writer.writeToWorkbook(originalTemplate);
        File file = new File(Definitions.OUTPUT_DIR, "ContractDefinition.xlsx");
        log.info("Writing modified Excel file: " + file.getAbsolutePath());
        workbook.getPOIWorkbook().write(new FileOutputStream(file));

        workbook = new ExcelWorkbook(file);
        TemplateDefinitionExcelReader reader = new TemplateDefinitionExcelReader();
        TemplateDefinition template = reader.readFromWorkbook(workbook);
        assertEquals(originalTemplate.getId(), template.getId());
        assertEquals(originalTemplate.getName(), template.getName());
        assertEquals(originalTemplate.getDescription(), template.getDescription());
        assertEquals(originalTemplate.getFilenamePattern(), template.getFilenamePattern());
        assertEquals(originalTemplate.getVariableDefinitions().size(), template.getVariableDefinitions().size());
        for (int i = 0; i < originalTemplate.getVariableDefinitions().size(); i++) {
            assertVariable(originalTemplate.getVariableDefinitions().get(i), template.getVariableDefinitions().get(i));
        }
        assertEquals(originalTemplate.getDependentVariableDefinitions().size(), template.getDependentVariableDefinitions().size());
        for (int i = 0; i < originalTemplate.getDependentVariableDefinitions().size(); i++) {
            assertVariable(originalTemplate.getDependentVariableDefinitions().get(i), template.getDependentVariableDefinitions().get(i));
        }
        //file = new File(Definitions.OUTPUT_DIR, "TemplateDefinition-2.xlsx");
        //workbook.getPOIWorkbook().write(new FileOutputStream(file));
    }

    @Test
    public void getBooleanAsStringTest() {
        assertEquals("X", TemplateDefinitionExcelWriter.getBooleanAsString(true));
        assertEquals("", TemplateDefinitionExcelWriter.getBooleanAsString(false));
    }

    @Test
    public void getStringAsBooleanTest() {
        assertFalse(TemplateDefinitionExcelReader.getStringAsBoolean(null));
        assertFalse(TemplateDefinitionExcelReader.getStringAsBoolean(""));
        assertFalse(TemplateDefinitionExcelReader.getStringAsBoolean("no"));
        assertFalse(TemplateDefinitionExcelReader.getStringAsBoolean("-"));
        assertFalse(TemplateDefinitionExcelReader.getStringAsBoolean("sdlfkje9"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("X"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("x"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("Y"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("y"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("yes"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("Yes"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("J"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("j"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("Ja"));
        assertTrue(TemplateDefinitionExcelReader.getStringAsBoolean("ja"));
    }

    static TemplateDefinition create() {
        TemplateDefinition template = new TemplateDefinition();
        template.setName("Employment contract").setFilenamePattern("employment-contract-${Employee}").setDescription("This template is used for the generation of emloyee contracts.");
        VariableDefinition gender = createStringVariable("Gender", "Gender of the employee.", true, true).addAllowedValues("male", "female");
        template.add(gender);
        template.add(createStringVariable("Employee", "Name of the employee.", true, true));
        template.add(new VariableDefinition(VariableType.DATE, "Date").setDescription("Date of contract.").setRequired());
        template.add(new VariableDefinition(VariableType.DATE, "BeginDate").setDescription("Begin of the contract.").setRequired());
        template.add(new VariableDefinition(VariableType.INT, "WeeklyHours").setDescription("The weekly working hours.").setRequired().setMinimumValue(1).setMaximumValue(40));
        template.add(new VariableDefinition(VariableType.INT, "NumberOfLeaveDays").setDescription("The number of leave days per year.").setRequired().setMinimumValue(20).setMaximumValue(30));
        template.add(new DependentVariableDefinition().setName("Mr_Mrs").setDependsOn(gender).addMapping("male", "Mr.").addMapping("female", "Mrs."));
        template.add(new DependentVariableDefinition().setName("He_She").setDependsOn(gender).addMapping("male", "He").addMapping("female", "She"));
        template.add(new DependentVariableDefinition().setName("he_she").setDependsOn(gender).addMapping("male", "he").addMapping("female", "she"));
        template.add(new DependentVariableDefinition().setName("His_Her").setDependsOn(gender).addMapping("male", "His").addMapping("female", "Her"));
        template.add(new DependentVariableDefinition().setName("his_her").setDependsOn(gender).addMapping("male", "his").addMapping("female", "her"));
        return template;
    }

    private static VariableDefinition createStringVariable(String name, String description, boolean required, boolean unique) {
        return new VariableDefinition(name)
                .setDescription(description)
                .setRequired(required).setUnique(unique);
    }

    private void assertVariable(VariableDefinition exp, VariableDefinition act) {
        assertEquals(exp.getName(), act.getName());
        assertEquals(exp.getDescription(), act.getDescription());
        assertEquals(exp.isRequired(), act.isRequired());
        assertEquals(exp.isUnique(), act.isUnique());
        assertEquals(exp.getType(), act.getType());
        log.debug("Variable: " + exp.getName());
        assertEquals(exp.getMinimumValue(), act.getMinimumValue());
        assertEquals(exp.getMaximumValue(), act.getMaximumValue());
        if (exp.getAllowedValuesList() != null) {
            assertNotNull(act.getAllowedValuesList());
            assertEquals(exp.getAllowedValuesList().size(), act.getAllowedValuesList().size());
            for (int i = 0; i < exp.getAllowedValuesList().size(); i++) {
                assertEquals(exp.getAllowedValuesList().get(i), act.getAllowedValuesList().get(i));
            }
        }
    }

    private void assertVariable(DependentVariableDefinition exp, DependentVariableDefinition act) {
        assertEquals(exp.getName(), act.getName());
        assertEquals(exp.getDependsOn() != null, act.getDependsOn() != null);
        assertEquals(exp.getDependsOn().getName(), act.getDependsOn().getName());
        if (exp.getMappingList() != null) {
            assertNotNull(act.getMappingList());
            assertEquals(exp.getMappingList().size(), act.getMappingList().size());
            for (int i = 0; i < exp.getMappingList().size(); i++) {
                assertEquals(exp.getMappingList().get(i), act.getMappingList().get(i));
            }
        }
    }
}

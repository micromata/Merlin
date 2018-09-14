package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialDataExcelReader {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelReader.class);

    private ExcelWorkbook workbook;
    private TemplateDefinition template;

    public TemplateDefinition readTemplateReference(ExcelWorkbook workbook) {
        TemplateDefinitionExcelReader templateDefinitionExcelReader = new TemplateDefinitionExcelReader();
        TemplateDefinition templateDefinition = templateDefinitionExcelReader.readConfigFromWorkbook(workbook);
        return templateDefinition;
    }

    public SerialData readFromWorkbook(ExcelWorkbook workbook, TemplateDefinition templateDefinition) {
        this.workbook = workbook;
        TemplateDefinitionExcelReader templateDefinitionExcelReader = new TemplateDefinitionExcelReader();
        SerialData serialData = new SerialData();
        serialData.setTemplateDefinition(templateDefinition);
        readVariables(templateDefinition);
        return serialData;
    }

    private void readVariables(TemplateDefinition templateDefinition) {
        ExcelSheet sheet = workbook.getSheet("Variables");
        for (VariableDefinition variableDefinition : template.getVariableDefinitions()) {
            ExcelColumnValidator validator;
            if (CollectionUtils.isNotEmpty(variableDefinition.getAllowedValuesList())) {
                validator = new ExcelColumnOptionsValidator(variableDefinition.getAllowedValuesList());
            } else {
                validator = new ExcelColumnValidator();
            }
            validator.setRequired(variableDefinition.isRequired());
            validator.setUnique(variableDefinition.isUnique());
            ExcelColumnDef variableCol = sheet.registerColumn(variableDefinition.getName(), validator);
        }
/*
        ExcelColumnDef variableCol = sheet.registerColumn("Variable",
                new ExcelColumnPatternValidator(RunsProcessor.IDENTIFIER_REGEXP).setRequired().setUnique());
        ExcelColumnDef descriptionCol = sheet.registerColumn("Description");
        ExcelColumnDef requiredCol = sheet.registerColumn("required");
        ExcelColumnDef uniqueCol = sheet.registerColumn("unique");
        ExcelColumnDef typeCol = sheet.registerColumn("Type");
        ExcelColumnDef valuesCol = sheet.registerColumn("Values");

        ExcelColumnDef minimumCol = sheet.registerColumn("Minimum");
        ExcelColumnDef maximumCol = sheet.registerColumn("Maximum");
        sheet.registerColumn("type", new ExcelColumnOptionsValidator("string", "int", "float", "date"));
        sheet.analyze(true);
        for (ExcelValidationErrorMessage msg : sheet.getAllValidationErrors()) {
            log.error(msg.getMessageWithAllDetails(I18n.getDefault()));
        }

        int counter = 0;
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            String name = PoiHelper.getValueAsString(sheet.getCell(row, variableCol));
            String description = PoiHelper.getValueAsString(sheet.getCell(row, descriptionCol));
            String valuesString = PoiHelper.getValueAsString(sheet.getCell(row, valuesCol));
            String typeString = PoiHelper.getValueAsString(sheet.getCell(row, typeCol));
            boolean required = getStringAsBoolean(PoiHelper.getValueAsString(sheet.getCell(row, requiredCol)));
            boolean unique = getStringAsBoolean(PoiHelper.getValueAsString(sheet.getCell(row, uniqueCol)));
            VariableDefinition variable = new VariableDefinition();
            variable.setName(name);
            variable.setType(typeString);
            variable.setDescription(description);
            variable.setRequired(required);
            variable.setUnique(unique);
            Object minimum = PoiHelper.getValue(sheet.getCell(row, minimumCol));
            variable.setMinimumValue(VariableDefinition.convertValue(minimum, variable.getType()));
            Object maximum = PoiHelper.getValue(sheet.getCell(row, maximumCol));
            variable.setMaximumValue(VariableDefinition.convertValue(maximum, variable.getType()));
            String[] values = CSVStringUtils.parseStringList(valuesString);
            if (values != null) {
                variable.addAllowedValues((Object[]) values);
            }
            template.add(variable);
        }*/
    }
}

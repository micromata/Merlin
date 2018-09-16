package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.csv.CSVStringUtils;
import de.reinhard.merlin.data.PropertiesStorage;
import de.reinhard.merlin.excel.*;
import de.reinhard.merlin.word.RunsProcessor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class TemplateDefinitionExcelReader {
    private Logger log = LoggerFactory.getLogger(TemplateDefinitionExcelReader.class);

    private ExcelWorkbook workbook;
    private TemplateDefinition template;
    private TemplateContext templateContext = new TemplateContext();
    private boolean validTemplate = true;

    public TemplateContext getTemplateContext() {
        return templateContext;
    }

    public TemplateDefinition readFromWorkbook(ExcelWorkbook workbook) {
        template = readConfigFromWorkbook(workbook);
        if (template == null) {
            log.error("No template definition found. Not a valid Merlin template.");
            validTemplate = false;
            return null;
        }
        readVariables();
        readDependentVariables();
        return template;
    }

    public TemplateDefinition readConfigFromWorkbook(ExcelWorkbook workbook) {
        this.workbook = workbook;
        ExcelSheet sheet = workbook.getSheet("Configuration");
        if (sheet == null) {
            return null;
        }
        template = new TemplateDefinition();
        ExcelConfigReader configReader = new ExcelConfigReader(sheet,
                "Variable", "Value");
        for (ExcelValidationErrorMessage msg : configReader.getSheet().getAllValidationErrors()) {
            log.error(msg.getMessageWithAllDetails(I18n.getDefault()));
        }
        PropertiesStorage props = configReader.readConfig(workbook);
        template.setId(props.getConfigString("Id"));
        template.setName(props.getConfigString("Name"));
        template.setDescription(props.getConfigString("Description"));
        template.setFilenamePattern(props.getConfigString("Filename"));
        if (StringUtils.isBlank(template.getId()) ||
                StringUtils.isBlank(template.getName())) {
            validTemplate = false;
            log.error("Sheet Configuration doesn't contain id and name. Not a valid Merlin template.");
        } else if (!configReader.isValid()) {
            validTemplate = false;
            log.error("Sheet Configuration isn't valid. Not a valid Merlin template:");
            for (ExcelValidationErrorMessage errorMessage : configReader.getSheet().getAllValidationErrors()) {
                log.error(errorMessage.getMessageWithAllDetails(templateContext.getI18n()));
            }
        }
        return template;
    }

    private void readVariables() {
        ExcelSheet sheet = workbook.getSheet("Variables");
        if (sheet == null) {
            log.info("Sheet 'Variables' not found. Not a valid Merlin template.");
            validTemplate = false;
            return;
        }
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
            variable.setMinimumValue(templateContext.convertValue(minimum, variable.getType()));
            Object maximum = PoiHelper.getValue(sheet.getCell(row, maximumCol));
            variable.setMaximumValue(templateContext.convertValue(maximum, variable.getType()));
            String[] values = CSVStringUtils.parseStringList(valuesString);
            if (values != null) {
                variable.addAllowedValues((Object[]) values);
            }
            template.add(variable);
        }
    }

    private void readDependentVariables() {
        ExcelSheet sheet = workbook.getSheet("Dependent Variables");
        if (sheet == null) {
            return;
        }
        ExcelColumnDef variableCol = sheet.registerColumn("Variable",
                new ExcelColumnPatternValidator(RunsProcessor.IDENTIFIER_REGEXP).setRequired().setUnique());
        ExcelColumnDef dependsOnCol = sheet.registerColumn("Depends on variable");
        ExcelColumnDef mappingCol = sheet.registerColumn("Mapping values");
        sheet.analyze(true);
        for (ExcelValidationErrorMessage msg : sheet.getAllValidationErrors()) {
            log.error(msg.getMessageWithAllDetails(I18n.getDefault()));
        }

        int counter = 0;
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            String name = PoiHelper.getValueAsString(sheet.getCell(row, variableCol));
            String valuesString = PoiHelper.getValueAsString(sheet.getCell(row, mappingCol));
            String[] values = CSVStringUtils.parseStringList(valuesString);
            String dependsOnString = PoiHelper.getValueAsString(sheet.getCell(row, dependsOnCol));
            VariableDefinition master = template.getVariableDefinition(dependsOnString);
            DependentVariableDefinition variable = new DependentVariableDefinition();
            variable.setName(name);
            variable.setDependsOn(master);
            if (master != null && master.getAllowedValuesList() != null) {
                for (int i = 0; i < master.getAllowedValuesList().size() && i < values.length; i++) {
                    variable.addMapping(master.getAllowedValuesList().get(i), values[i]);
                }
            }
            template.add(variable);
        }
    }

    public static boolean getStringAsBoolean(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        String lower = value.toLowerCase();
        if (lower.startsWith("x") ||
                lower.startsWith("y") || // yes
                lower.startsWith("j")) { // ja - German yers.
            return true;
        }
        return false;
    }

    /**
     * @return false, if any required settings are missed.
     */
    public boolean isValidTemplate() {
        return validTemplate;
    }
}

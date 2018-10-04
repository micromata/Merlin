package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.csv.CSVStringUtils;
import de.reinhard.merlin.data.PropertiesStorage;
import de.reinhard.merlin.excel.*;
import de.reinhard.merlin.utils.ReplaceUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class TemplateDefinitionExcelReader {
    private Logger log = LoggerFactory.getLogger(TemplateDefinitionExcelReader.class);

    private ExcelWorkbook workbook;
    private TemplateDefinition templateDefinition;
    private TemplateRunContext templateRunContext = new TemplateRunContext();
    private boolean validTemplateDefinition = true;
    private ExcelConfigReader excelConfigReader;

    public TemplateRunContext getTemplateRunContext() {
        return templateRunContext;
    }

    public boolean isMerlinTemplateDefinition(ExcelWorkbook workbook) {
        if (workbook.getSheet("Variables") == null) {
            return false;
        }
        if (workbook.getSheet("Configuration") == null) {
            return false;
        }
        templateDefinition = readConfigFromWorkbook(workbook);
        if (templateDefinition == null || StringUtils.isBlank(templateDefinition.getId())) {
            return false;
        }
        return true;
    }

    public TemplateDefinition readFromWorkbook(ExcelWorkbook workbook) {
        return readFromWorkbook(workbook, true);
    }

    /**
     * @param workbook
     * @param templateDefinitionRequired if true, a templateDefinition is required (default) and an error message will be logged
     *                                   if no valid template definition found. If false, you may check, if an Excel
     *                                   file is a template definition file (used by DirectoryScanner).
     * @return
     */
    public TemplateDefinition readFromWorkbook(ExcelWorkbook workbook, boolean templateDefinitionRequired) {
        templateDefinition = readConfigFromWorkbook(workbook);
        if (templateDefinition == null) {
            if (templateDefinitionRequired) {
                log.error("No template definition found. Not a valid Merlin template.");
            }
            validTemplateDefinition = false;
            return null;
        }
        readVariables();
        readDependentVariables();
        return templateDefinition;
    }

    public TemplateDefinition readConfigFromWorkbook(ExcelWorkbook workbook) {
        this.workbook = workbook;
        ExcelSheet sheet = workbook.getSheet("Configuration");
        if (sheet == null) {
            return null;
        }
        templateDefinition = new TemplateDefinition();
        if (excelConfigReader == null) {
            excelConfigReader = new ExcelConfigReader(sheet,
                    "Variable", "Value");
            for (ExcelValidationErrorMessage msg : excelConfigReader.getSheet().getAllValidationErrors()) {
                log.error(msg.getMessageWithAllDetails(I18n.getDefault()));
            }
        }
        PropertiesStorage props = excelConfigReader.readConfig(workbook);
        templateDefinition.setId(props.getConfigString("Id"));
        templateDefinition.setDescription(props.getConfigString("Description"));
        templateDefinition.setFilenamePattern(props.getConfigString("FilenamePattern"));
        if (StringUtils.isBlank(templateDefinition.getId())) {
            validTemplateDefinition = false;
            log.error("Sheet Configuration doesn't contain Id. Not a valid Merlin template definition.");
        } else if (!excelConfigReader.isValid()) {
            validTemplateDefinition = false;
            log.error("Sheet Configuration isn't valid. Not a valid Merlin template:");
            for (ExcelValidationErrorMessage errorMessage : excelConfigReader.getSheet().getAllValidationErrors()) {
                log.error(errorMessage.getMessageWithAllDetails(templateRunContext.getI18n()));
            }
        }
        return templateDefinition;
    }

    private void readVariables() {
        ExcelSheet sheet = workbook.getSheet("Variables");
        if (sheet == null) {
            log.info("Sheet 'Variables' not found. Not a valid Merlin template.");
            validTemplateDefinition = false;
            return;
        }
        ExcelColumnDef variableCol = sheet.registerColumn("Variable",
                new ExcelColumnPatternValidator(ReplaceUtils.IDENTIFIER_REGEXP).setRequired().setUnique());
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
            variable.setTypeFromString(typeString);
            variable.setDescription(description);
            variable.setRequired(required);
            variable.setUnique(unique);
            Object minimum = PoiHelper.getValue(sheet.getCell(row, minimumCol));
            variable.setMinimumValue(templateRunContext.convertValue(minimum, variable.getType()));
            Object maximum = PoiHelper.getValue(sheet.getCell(row, maximumCol));
            variable.setMaximumValue(templateRunContext.convertValue(maximum, variable.getType()));
            String[] values = CSVStringUtils.parseStringList(valuesString);
            if (values != null) {
                variable.addAllowedValues((Object[]) values);
            }
            templateDefinition.add(variable);
        }
    }

    private void readDependentVariables() {
        ExcelSheet sheet = workbook.getSheet("Dependent Variables");
        if (sheet == null) {
            return;
        }
        ExcelColumnDef variableCol = sheet.registerColumn("Variable",
                new ExcelColumnPatternValidator(ReplaceUtils.IDENTIFIER_REGEXP).setRequired().setUnique());
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
            VariableDefinition master = templateDefinition.getVariableDefinition(dependsOnString);
            DependentVariableDefinition variable = new DependentVariableDefinition();
            variable.setName(name);
            variable.setDependsOn(master);
            if (master != null && master.getAllowedValuesList() != null) {
                for (int i = 0; i < master.getAllowedValuesList().size() && i < values.length; i++) {
                    variable.addMapping(master.getAllowedValuesList().get(i), values[i]);
                }
            }
            templateDefinition.add(variable);
        }
    }

    public static boolean getStringAsBoolean(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        String lower = value.toLowerCase();
        if (lower.startsWith("x") ||
                lower.startsWith("y") || // yes
                lower.startsWith("j")) { // ja - German yes.
            return true;
        }
        return false;
    }

    /**
     * @return false, if any required settings are missed.
     */
    public boolean isValidTemplateDefinition() {
        return validTemplateDefinition;
    }
}

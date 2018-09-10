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

    private static final int COLUMN_WIDE_LENGTH = 5000;
    private static final int COLUMN_EXTRA_WIDE_LENGTH = 15000;

    private ExcelWorkbook workbook;
    private TemplateDefinition template;

    public TemplateDefinition readFromWorkbook(ExcelWorkbook workbook) {
        this.workbook = workbook;
        template = new TemplateDefinition();
        ExcelConfigReader configReader = new ExcelConfigReader(workbook.getSheet("Configuration"),
                "Variable", "Value");
        for (ExcelValidationErrorMessage msg : configReader.getSheet().getAllValidationErrors()) {
            log.error(msg.getMessageWithAllDetails(I18n.getDefault()));
        }
        PropertiesStorage props = configReader.readConfig(workbook);
        template.setId(props.getConfig("Id"));
        template.setName(props.getConfig("Name"));
        template.setDescription(props.getConfig("Description"));
        template.setFilenamePattern(props.getConfig("Filename"));
        readVariables();
        return template;
    }

    private void readVariables() {
        ExcelSheet sheet = workbook.getSheet("Variables");
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
}

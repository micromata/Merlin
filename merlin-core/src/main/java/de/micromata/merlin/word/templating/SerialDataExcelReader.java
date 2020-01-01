package de.micromata.merlin.word.templating;

import de.micromata.merlin.CoreI18n;
import de.micromata.merlin.I18n;
import de.micromata.merlin.data.PropertiesStorage;
import de.micromata.merlin.excel.*;
import de.micromata.merlin.utils.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SerialDataExcelReader {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelReader.class);

    private ExcelWorkbook workbook;
    private SerialData serialData;
    private TemplateRunContext templateRunContext = new TemplateRunContext();
    private ExcelConfigReader excelConfigReader;
    private I18n i18n;

    public static boolean isMerlinSerialRunDefinition(ExcelWorkbook workbook) {
        if (workbook.getSheetByLocalizedNames(SerialDataExcelWriter.SERIAL_VARIABLES_SHEET_NAME) == null) {
            return false;
        }
        if (workbook.getSheetByLocalizedNames(SerialDataExcelWriter.CONFIGURATION_SHEET_NAME) == null) {
            return false;
        }
        return true;
    }

    public SerialDataExcelReader(ExcelWorkbook workbook) {
        this.workbook = workbook;
        this.i18n = CoreI18n.getDefault();
        readConfigFromWorkbook();
    }

    public TemplateRunContext getTemplateRunContext() {
        return templateRunContext;
    }

    public boolean isValidMerlinSerialTemplateData() {
        if (isMerlinSerialRunDefinition(workbook) == false) {
            return false;
        }
        if (serialData == null || serialData.getFilenamePattern() == null) {
            return false;
        }
        return true;
    }

    /**
     * @param templateStatistics Needed for validating that all required variables are given.
     */
    public void readVariables(TemplateStatistics templateStatistics) {
        ExcelSheet sheet = workbook.getSheetByLocalizedNames(SerialDataExcelWriter.SERIAL_VARIABLES_SHEET_NAME);
        if (sheet == null) {
            log.error("Can't read variables from serial template. Excel sheet with variables not found (may-be misspelled?");
            return;
        }
        Map<VariableDefinition, ExcelColumnDef> columnDefMap = new HashMap<>();
        for (VariableDefinition variableDefinition : templateStatistics.getInputVariables()) {
            ExcelColumnValidator validator;
            if (CollectionUtils.isNotEmpty(variableDefinition.getAllowedValuesList())) {
                validator = new ExcelColumnOptionsValidator(variableDefinition.getAllowedValuesList());
            } else if (variableDefinition.getType() == VariableType.DATE) {
                validator = new ExcelColumnDateValidator();
            } else if (variableDefinition.getType().isIn(VariableType.INT, VariableType.FLOAT)) {
                validator = new ExcelColumnNumberValidator();
                Double minimumValue = Converter.createDouble(variableDefinition.getMinimumValue());
                if (minimumValue != null) {
                    ((ExcelColumnNumberValidator) validator).setMinimum(minimumValue);
                }
                Double maximumValue = Converter.createDouble(variableDefinition.getMaximumValue());
                if (maximumValue != null) {
                    ((ExcelColumnNumberValidator) validator).setMaximum(maximumValue);
                }
            } else {
                validator = new ExcelColumnValidator();
            }
            validator.setRequired(variableDefinition.isRequired());
            validator.setUnique(variableDefinition.isUnique());
            ExcelColumnDef variableCol = sheet.registerColumn(variableDefinition.getName(), validator);
            columnDefMap.put(variableDefinition, variableCol);
        }
        sheet.analyze(true);
        for (ExcelValidationErrorMessage msg : sheet.getAllValidationErrors()) {
            log.error(msg.getMessageWithAllDetails(i18n));
        }
        int counter = 0;
        DataFormatter df = new DataFormatter();
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            Variables variables = new Variables();
            for (VariableDefinition variableDefinition : templateStatistics.getInputVariables()) {
                ExcelColumnDef columnDef = columnDefMap.get(variableDefinition);
                Cell cell = sheet.getCell(row, columnDef);
                String formattedCellValue = templateRunContext.getFormattedValue(cell);
                variables.putFormatted(variableDefinition.getName(), formattedCellValue);
                Object value = PoiHelper.getValue(cell, false);
                if (value == null) {
                    continue;
                }
                Object val = templateRunContext.convertValue(value, variableDefinition.getType());
                variables.put(variableDefinition.getName(), val);
            }
            serialData.add(variables);
        }
    }

    private void readConfigFromWorkbook() {
        ExcelSheet sheet = workbook.getSheetByLocalizedNames(AbstractExcelWriter.CONFIGURATION_SHEET_NAME);
        if (sheet == null) {
            return;
        }
        serialData = new SerialData();
        if (excelConfigReader == null) {
            excelConfigReader = new ExcelConfigReader(sheet,
                    "Variable", "Value");
            for (ExcelValidationErrorMessage msg : excelConfigReader.getSheet().getAllValidationErrors()) {
                log.error(msg.getMessageWithAllDetails(i18n));
            }
        }
        PropertiesStorage props = excelConfigReader.readConfig(workbook);
        serialData.setReferencedTemplatePrimaryKey(props.getConfigString("Template"));
        serialData.setReferencedTemplateDefinitionPrimaryKey(props.getConfigString("TemplateDefinition"));
        serialData.setFilenamePattern(props.getConfigString("FilenamePattern"));
    }

    public SerialData getSerialData() {
        return serialData;
    }
}

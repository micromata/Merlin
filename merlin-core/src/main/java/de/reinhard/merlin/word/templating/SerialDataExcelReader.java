package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.data.PropertiesStorage;
import de.reinhard.merlin.excel.*;
import de.reinhard.merlin.utils.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SerialDataExcelReader {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelReader.class);

    private ExcelWorkbook workbook;
    private TemplateRunContext templateRunContext = new TemplateRunContext();

    public TemplateRunContext getTemplateRunContext() {
        return templateRunContext;
    }

    public TemplateDefinition readTemplateReference(ExcelWorkbook workbook) {
        TemplateDefinitionExcelReader templateDefinitionExcelReader = new TemplateDefinitionExcelReader();
        TemplateDefinition templateDefinition = templateDefinitionExcelReader.readConfigFromWorkbook(workbook);
        return templateDefinition;
    }

    public SerialData readFromWorkbook(ExcelWorkbook workbook, TemplateDefinition templateDefinition) {
        this.workbook = workbook;
        TemplateDefinitionExcelReader templateDefinitionExcelReader = new TemplateDefinitionExcelReader();
        SerialData serialData = readConfigFromWorkbook();
        readVariables(templateDefinition, serialData);
        return serialData;
    }

    private SerialData readConfigFromWorkbook() {
        ExcelSheet sheet = workbook.getSheet("Configuration");
        if (sheet == null) {
            return null;
        }
        SerialData serialData = new SerialData();
        ExcelConfigReader configReader = new ExcelConfigReader(sheet,
                "Variable", "Value");
        for (ExcelValidationErrorMessage msg : configReader.getSheet().getAllValidationErrors()) {
            log.error(msg.getMessageWithAllDetails(I18n.getDefault()));
        }
        PropertiesStorage props = configReader.readConfig(workbook);
        serialData.setTemplateCanonicalPath(props.getConfigString("Template"));
        serialData.setTemplateDefinitionId(props.getConfigString("TemplateDefinition"));
        serialData.setFilenamePattern(props.getConfigString("FilenamePattern"));
        return serialData;
    }


    private void readVariables(TemplateDefinition templateDefinition, SerialData serialData) {
        ExcelSheet sheet = workbook.getSheet("Variables");
        Map<VariableDefinition, ExcelColumnDef> columnDefMap = new HashMap<>();
        for (VariableDefinition variableDefinition : templateDefinition.getVariableDefinitions()) {
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
            log.error(msg.getMessageWithAllDetails(I18n.getDefault()));
        }
        int counter = 0;
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            SerialDataEntry serialDataEntry = new SerialDataEntry();
            for (VariableDefinition variableDefinition : templateDefinition.getVariableDefinitions()) {
                ExcelColumnDef columnDef = columnDefMap.get(variableDefinition);
                Object value = PoiHelper.getValue(sheet.getCell(row, columnDef));
                if (value == null) {
                    continue;
                }
                Object val = templateRunContext.convertValue(value, variableDefinition.getType());
                serialDataEntry.put(variableDefinition.getName(), val);
            }
            serialData.add(serialDataEntry);
        }
    }
}

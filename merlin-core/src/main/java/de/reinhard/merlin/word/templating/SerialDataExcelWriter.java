package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelCell;
import de.reinhard.merlin.excel.ExcelRow;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialDataExcelWriter extends AbstractExcelWriter {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelWriter.class);

    private SerialData serialData;

    /**
     * The properties in template are overwriting any settings in serialData.
     *
     * @param serialData
     */
    public SerialDataExcelWriter(SerialData serialData) {
        this.serialData = serialData;
    }

    public ExcelWorkbook writeToWorkbook() {
        super.init();
        Validate.notNull(serialData.getTemplate());
        TemplateDefinition templateDefinition = serialData.getTemplateDefinition();
        createVariablesSheet();
        createConfigurationSheet();
        addConfigRow("Template", serialData.getTemplate().getPrimaryKey(),
                "merlin.word.templating.reference.template.primaryKey");
        addConfigRow("TemplateDefinition", templateDefinition != null ? templateDefinition.getPrimaryKey() : "",
                "merlin.word.templating.reference.templateDefinition.primaryKey");
        addConfigRow("FilenamePattern", serialData.getFilenamePattern(), "merlin.word.templating.serial.config.filenamePattern");
        currentSheet.autosize();
        return workbook;
    }

    private void createVariablesSheet() {
        currentSheet = workbook.createOrGetSheet("Variables");
        addDescriptionRow("merlin.word.templating.sheet_serial_variables_description", -1, false);
        ExcelRow row = currentSheet.createRow();
        int numberOfColumns = 0;
        TemplateDefinition templateDefinition = serialData.getTemplateDefinition();
        if (templateDefinition == null) {
            templateDefinition = serialData.getTemplate().createAutoTemplateDefinition();
        }
        for (VariableDefinition variableDefinition : templateDefinition.getVariableDefinitions()) {
            row.createCells(headRowStyle, variableDefinition.getName());
            numberOfColumns++;
        }
        currentSheet.getRow(0).addMergeRegion(0, numberOfColumns - 1);
        currentSheet.getRow(1).setHeight(50).addMergeRegion(0, numberOfColumns - 1);
        if (serialData != null) {
            for (SerialDataEntry entry : serialData.getEntries()) {
                row = currentSheet.createRow();
                for (VariableDefinition variableDefinition : templateDefinition.getVariableDefinitions()) {
                    Object valueObject = entry.get(variableDefinition.getName());
                    ExcelCell cell = row.createCell();
                    templateRunContext.setCellValue(workbook, cell.getCell(), valueObject, variableDefinition.getType());
                }
            }
        }
        currentSheet.autosize();
        currentSheet.getPoiSheet().setActiveCell(new CellAddress(3, 0));
    }
}

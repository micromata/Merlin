package de.micromata.merlin.word.templating;

import de.micromata.merlin.excel.ExcelCell;
import de.micromata.merlin.excel.ExcelRow;
import de.micromata.merlin.excel.ExcelWorkbook;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialDataExcelWriter extends AbstractExcelWriter {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelWriter.class);
    static final String SERIAL_VARIABLES_SHEET_NAME = "merlin.word.templating.serial.sheet.serialVariables.name";

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
        if (serialData.getTemplate() != null) {
            addConfigRow("Template", serialData.getTemplate().getPrimaryKey(), serialData.getTemplate().getFileDescriptor().getFilename());
        } else {
            addConfigRow("Template", "", "merlin.word.templating.reference.template.primaryKey");
        }
        if (templateDefinition != null) {
            addConfigRow("TemplateDefinition", templateDefinition.getPrimaryKey(),
                    templateDefinition.getFileDescriptor().getFilename() + ": " + templateDefinition.getId());
        } else {
            addConfigRow("TemplateDefinition", "", "merlin.word.templating.reference.templateDefinition.primaryKey");
        }
        serialData.createFilenamePattern();
        addConfigRow("FilenamePattern", serialData.getFilenamePattern(), "merlin.word.templating.serial.config.filenamePattern");
        currentSheet.autosize();
        return workbook;
    }

    private void createVariablesSheet() {
        currentSheet = workbook.createOrGetSheet(getI18n().getMessage(SERIAL_VARIABLES_SHEET_NAME));
        addDescriptionRow("merlin.word.templating.sheet_serial_variables_description", -1, false);
        ExcelRow row = currentSheet.createRow();
        int numberOfColumns = 0;
        for (VariableDefinition variableDefinition : serialData.getTemplate().getStatistics().getInputVariables()) {
            row.createCells(headRowStyle, variableDefinition.getName());
            numberOfColumns++;
        }
        currentSheet.getRow(0).addMergeRegion(0, numberOfColumns - 1);
        currentSheet.getRow(1).setHeight(50).addMergeRegion(0, numberOfColumns - 1);
        if (serialData.getEntries() != null) {
            for (Variables entry : serialData.getEntries()) {
                row = currentSheet.createRow();
                for (VariableDefinition variableDefinition : serialData.getTemplate().getStatistics().getInputVariables()) {
                    Object valueObject = entry.get(variableDefinition.getName());
                    ExcelCell cell = row.createCell();
                    templateRunContext.setCellValue(workbook, cell.getCell(), valueObject, variableDefinition.getType());
                    numberOfColumns++;
                }
            }
        }
        currentSheet.autosize();
        currentSheet.getPoiSheet().setActiveCell(new CellAddress(3, 0));
    }
}

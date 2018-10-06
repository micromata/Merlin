package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelCell;
import de.reinhard.merlin.excel.ExcelRow;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialDataExcelWriter extends AbstractExcelWriter {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelWriter.class);

    private SerialData serialData;
    private Template template;
    private TemplateDefinition templateDefinition;

    /**
     * The properties in template are overwriting any settings in serialData.
     *
     * @param serialData
     * @param template
     */
    public SerialDataExcelWriter(SerialData serialData, Template template) {
        this(serialData, template, null);
    }

    /**
     * The properties in template are overwriting any settings in serialData.
     *
     * @param serialData
     * @param template
     * @param templateDefinition Any setting in template concerning template definition is ignored. This parameter is used instead.
     */
    public SerialDataExcelWriter(SerialData serialData, Template template, TemplateDefinition templateDefinition) {
        this.serialData = serialData;
        this.template = template;
        if (templateDefinition != null) {
            this.templateDefinition = templateDefinition;
        } else {
            this.templateDefinition = template.getTemplateDefinition();
            if (this.templateDefinition == null) {
                this.templateDefinition = template.createAutoTemplateDefinition();
            }
        }
    }

    public ExcelWorkbook writeToWorkbook() {
        super.init();
        createVariablesSheet();
        createConfigurationSheet();
        addConfigRow("Template", template.getFileDescriptor().getCanonicalPathString(), "merlin.word.templating.config.template");
        addConfigRow("TemplateDefinition", templateDefinition.getId(), templateDefinition.getDescription());
        addConfigRow("FilenamePattern", serialData.getFilenamePattern(), "merlin.word.templating.serial.config.filenamePattern");
        currentSheet.autosize();
        return workbook;
    }

    private void createVariablesSheet() {
        currentSheet = workbook.createOrGetSheet("Variables");
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_serial_variables_description", -1, false);
        row = currentSheet.createRow();
        int numberOfColumns = 0;
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

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

    public SerialDataExcelWriter(SerialData serialData) {
        this.serialData = serialData;
        this.template = serialData.getTemplate();
    }

    public ExcelWorkbook writeToWorkbook() {
        super.init();
        createVariablesSheet();
        createConfigurationSheet();
        addConfigRow("Template", serialData.getCanonicalTemplatePath(), null);
        addConfigRow("TemplateDefinition", serialData.getTemplateDefinitionId(), serialData.getTemplateDefinitionName());
        addConfigRow("Filename", serialData.getFilenamePattern(), null);
        currentSheet.autosize();
        return workbook;
    }

    private void createVariablesSheet() {
        currentSheet = workbook.createOrGetSheet("Variables");
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_serial_variables_description", -1, false);
        row = currentSheet.createRow();
        int numberOfColumns = 0;
        TemplateDefinition templateDefinition = template.getTemplateDefinition();
        if (templateDefinition == null) {
            templateDefinition = template.createAutoTemplateDefinition();
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

package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelCell;
import de.reinhard.merlin.excel.ExcelRow;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialDataExcelWriter extends AbstractExcelWriter {
    private Logger log = LoggerFactory.getLogger(SerialDataExcelWriter.class);

    private TemplateDefinition template;

    public ExcelWorkbook writeToWorkbook(TemplateDefinition template) {
        return writeToWorkbook(template, null);
    }

    public ExcelWorkbook writeToWorkbook(TemplateDefinition template, SerialData serialData) {
        this.template = template;
        super.init();
        createVariablesSheet(serialData);
        createConfigurationSheet();
        addConfigRow("Name", template.getName(), null);
        addConfigRow("Description", template.getDescription(), null);
        addConfigRow("Filename", template.getFilenamePattern(), null);
        ExcelCell cell = addConfigRow("Id", template.getId(), "merlin.word.templating.please_do_not_modify_id");
        cell.setCellStyle(warningCellStyle);
        currentSheet.autosize();
        return workbook;
    }

    private void createVariablesSheet(SerialData serialData) {
        currentSheet = workbook.createOrGetSheet("Variables");
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_serial_variables_description", -1, false);
        row = currentSheet.createRow();
        int numberOfColumns = 0;
        for (VariableDefinition variableDefinition : template.getVariableDefinitions()) {
            row.createCells(headRowStyle, variableDefinition.getName());
            numberOfColumns++;
        }
        currentSheet.getRow(0).addMergeRegion(0, numberOfColumns - 1);
        currentSheet.getRow(1).setHeight(50).addMergeRegion(0, numberOfColumns - 1);
        if (serialData != null) {
            for (SerialDataEntry entry : serialData.getEntries()) {
                row = currentSheet.createRow();
                for (VariableDefinition variableDefinition : template.getVariableDefinitions()) {
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

package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.excel.ExcelCell;
import de.reinhard.merlin.excel.ExcelRow;
import de.reinhard.merlin.excel.ExcelSheet;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractExcelWriter {
    private Logger log = LoggerFactory.getLogger(AbstractExcelWriter.class);

    static final String CONFIGURATION_SHEET_NAME = "merlin.word.templating.sheet.configuration.name";

    protected static final int COLUMN_WIDE_LENGTH = 5000;
    protected static final int COLUMN_EXTRA_WIDE_LENGTH = 15000;

    protected ExcelWorkbook workbook;
    protected CellStyle titleStyle;
    protected CellStyle headRowStyle;
    protected CellStyle warningCellStyle;
    protected CellStyle descriptionStyle;
    protected ExcelSheet currentSheet; // Current working sheet.
    protected TemplateRunContext templateRunContext;

    public AbstractExcelWriter() {
        templateRunContext = new TemplateRunContext();
    }

    public TemplateRunContext getTemplateRunContext() {
        return templateRunContext;
    }

    protected void init() {
        Workbook poiWorkbook = new XSSFWorkbook();
        workbook = new ExcelWorkbook(poiWorkbook);
        titleStyle = createCellStyle(IndexedColors.ROYAL_BLUE.index, true, false, false, 24);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headRowStyle = createCellStyle(null, true, false, false, null);
        warningCellStyle = createCellStyle(IndexedColors.RED.index, true, false, false, null);
        descriptionStyle = createCellStyle(IndexedColors.BLUE_GREY.index, false, true, true, null);
    }

    protected ExcelRow addDescriptionRow(String descriptionKey, int numberOfColumns) {
        return addDescriptionRow(descriptionKey, numberOfColumns, true);
    }

    protected ExcelRow addDescriptionRow(String descriptionKey, int numberOfColumns, boolean dontModify) {
        ExcelRow row = currentSheet.createRow();
        row.createCell().setCellStyle(titleStyle).setCellValue("Merlin");
        row.setHeight(50);
        if (numberOfColumns > 0) {
            row.addMergeRegion(0, numberOfColumns - 1);
        }
        row = currentSheet.createRow();
        String msg;
        if (dontModify) {
            msg = getI18n().getMessage(descriptionKey)
                    + "\n"
                    + getI18n().getMessage("merlin.word.templating.sheet_configuration_hint");
        } else {
            msg = getI18n().getMessage(descriptionKey);
        }
        row.createCell().setCellStyle(descriptionStyle).setCellValue(msg);
        if (numberOfColumns > 0) {
            row.setHeight(80).addMergeRegion(0, numberOfColumns - 1);
        }
        return row;
    }

    /**
     * @param variable
     * @param value
     * @param description If i18n key, the translation will be used, otherwise the description itself.
     * @return
     */
    protected ExcelCell addConfigRow(String variable, Object value, String description) {
        ExcelRow row = currentSheet.createRow();
        // Variable
        row.createCell().setCellValue(variable);
        // Value
        if (value instanceof Boolean) {
            row.createCell().setCellValue(workbook, (Boolean) value);
        } else {
            row.createCell().setCellValue(value == null ? "" : String.valueOf(value));
        }
        // Description
        ExcelCell cell = row.createCell();
        if (description != null) {
            if (getI18n().containsMessage(description)) {
                cell.setCellValue(getI18n().getMessage(description));
            } else {
                cell.setCellValue(description);
            }
        } else {
            cell.setCellValue("");
        }
        return cell;
    }

    protected CellStyle createCellStyle(Short color, boolean bold, boolean italic, boolean wrapText, Integer fontSize) {
        CellStyle style = workbook.getPOIWorkbook().createCellStyle();
        Font font = workbook.getPOIWorkbook().createFont();
        if (color != null) {
            font.setColor(color);
        }
        font.setBold(bold);
        font.setItalic(italic);
        style.setFont(font);
        if (wrapText) {
            style.setWrapText(true);
            style.setVerticalAlignment(VerticalAlignment.TOP);
        }
        if (fontSize != null) {
            font.setFontHeightInPoints(fontSize.shortValue());
        }
        return style;
    }

    protected void writeValue(ExcelCell cell, Object value, VariableType type) {
        Object targetValue = templateRunContext.convertValue(value, type);
        if (targetValue == null) {
            return;
        }
        switch (type) {
            case STRING:
                cell.setCellValue((String) targetValue);
                break;
            case INT:
                cell.setCellValue(workbook, (Integer) targetValue);
                break;
            case FLOAT:
                cell.setCellValue(workbook, (Double) targetValue);
                break;
            case DATE:
                log.error("Date not yet implemented.");
        }
    }


    protected void createConfigurationSheet() {
        currentSheet = workbook.createOrGetSheet(getI18n().getMessage(CONFIGURATION_SHEET_NAME));
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_configuration_description", 3);
        row = currentSheet.createRow();
        row.createCells(headRowStyle, "Variable", "Value", "Description");
    }

    protected I18n getI18n() {
        return this.templateRunContext.getI18n();
    }
}

package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.excel.ExcelCell;
import de.reinhard.merlin.excel.ExcelRow;
import de.reinhard.merlin.excel.ExcelSheet;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class TemplateDefinitionExcelWriter {
    private Logger log = LoggerFactory.getLogger(TemplateDefinitionExcelWriter.class);

    private static final int COLUMN_WIDE_LENGTH = 5000;
    private static final int COLUMN_EXTRA_WIDE_LENGTH = 15000;

    private ExcelWorkbook workbook;
    private CellStyle titleStyle;
    private CellStyle headRowStyle;
    private CellStyle warningCellStyle;
    private CellStyle descriptionStyle;
    private ExcelSheet sheet;
    private TemplateDefinition template;

    public ExcelWorkbook writeToWorkbook(TemplateDefinition template) {
        this.template = template;
        Workbook poiWorkbook = new XSSFWorkbook();
        workbook = new ExcelWorkbook(poiWorkbook);
        titleStyle = createCellStyle(IndexedColors.ROYAL_BLUE.index, true, false, false, 24);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headRowStyle = createCellStyle(null, true, false, false, null);
        warningCellStyle = createCellStyle(IndexedColors.RED.index, true, false, false, null);
        descriptionStyle = createCellStyle(IndexedColors.BLUE_GREY.index, false, true, true, null);
        createVariablesSheet();
        createDependentVariablesSheet();
        createConfigurationSheet();
        return workbook;
    }

    private void createVariablesSheet() {
        sheet = workbook.createOrGetSheet("Variables");
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_variables_description", 8);
        row = sheet.createRow();
        row.createCells(headRowStyle, "Variable", "Type", "required", "unique", "Values", "Minimum", "Maximum", "Description");
        sheet.autosize();
        sheet.setColumnWidth(0, COLUMN_WIDE_LENGTH);
        sheet.setColumnWidth(4, COLUMN_EXTRA_WIDE_LENGTH);
        sheet.setColumnWidth(7, COLUMN_EXTRA_WIDE_LENGTH);
        for (VariableDefinition variableDefinition : template.getVariableDefinitions()) {
            row = sheet.createRow();
            // Variable
            row.createCell().setCellValue(variableDefinition.getName());
            // type
            row.createCell().setCellValue(variableDefinition.getTypeAsString());
            // required
            row.createCell().setCellValue(getBooleanAsString(variableDefinition.isRequired()));
            // unique
            row.createCell().setCellValue(getBooleanAsString(variableDefinition.isUnique()));
            // Allowed values
            String allowedValues;
            if (CollectionUtils.isEmpty(variableDefinition.getAllowedValuesList())) {
                allowedValues = "";
            } else {
                allowedValues = variableDefinition.getAllowedValuesList().stream()
                        .map(s -> "\"" + s + "\"")
                        .collect(Collectors.joining(", "));
            }
            row.createCell().setCellValue(allowedValues);
            // Minimum
            writeValue(row.createCell(), variableDefinition.getMinimumValue(), variableDefinition.getType());
            // Maximum
            writeValue(row.createCell(), variableDefinition.getMaximumValue(), variableDefinition.getType());
            // Description
            row.createCell().setCellValue(variableDefinition.getDescription());

        }
        sheet.getPoiSheet().setActiveCell(new CellAddress(3, 0));
    }

    private void createDependentVariablesSheet() {
        // Dependent variables.
        sheet = workbook.createOrGetSheet("Dependent Variables");
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_dependent_variables_description", 4);
        row = sheet.createRow();
        row.createCells(headRowStyle, "Variable", "Depends on variable", "Mapping values", I18n.getDefault().getMessage("merlin.word.templating.sheet_dependent_variables_mapping"));
        sheet.autosize();
        sheet.setColumnWidth(0, COLUMN_WIDE_LENGTH);
        sheet.setColumnWidth(1, COLUMN_WIDE_LENGTH);
        sheet.setColumnWidth(2, COLUMN_WIDE_LENGTH);
        sheet.setColumnWidth(3, COLUMN_EXTRA_WIDE_LENGTH);
        for (DependentVariableDefinition variableDefinition : template.getDependentVariableDefinitions()) {
            row = sheet.createRow();
            // Variable
            row.createCell().setCellValue(variableDefinition.getName());
            // Depends on variable
            VariableDefinition dependsOnVariable = variableDefinition.getDependsOn();
            row.createCell().setCellValue(dependsOnVariable != null ? dependsOnVariable.getName() : "");
            // Mapping values
            String mappedValues;
            if (CollectionUtils.isEmpty(variableDefinition.getMappingList())) {
                mappedValues = "";
            } else {
                mappedValues = variableDefinition.getMappingList().stream()
                        .map(s -> "\"" + s + "\"")
                        .collect(Collectors.joining(", "));
            }
            row.createCell().setCellValue(mappedValues);
            row.createCell().setCellValue(variableDefinition.getMappingInformation());
        }
        sheet.getPoiSheet().setActiveCell(new CellAddress(3, 0));
    }

    private void createConfigurationSheet() {
        sheet = workbook.createOrGetSheet("Configuration");
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_configuration_description", 3);
        row = sheet.createRow();
        row.createCells(headRowStyle, "Variable", "Value", "Description");
        addConfigRow("Name", template.getName(), null);
        addConfigRow("Description", template.getDescription(), null);
        addConfigRow("Filename", template.getFilenamePattern(), null);
        ExcelCell cell = addConfigRow("Id", template.getId(), "merlin.word.templating.please_do_not_modify_id");
        cell.setCellStyle(warningCellStyle);
        sheet.autosize();
        sheet.getPoiSheet().setActiveCell(new CellAddress(3, 0));
    }

    private ExcelRow addDescriptionRow(String descriptionKey, int numberOfColumns) {
        ExcelRow row = sheet.createRow();
        row.createCell().setCellStyle(titleStyle).setCellValue("Merlin");
        row.setHeight(50).addMergeRegion(0, numberOfColumns - 1);
        row = sheet.createRow();
        row.createCell().setCellStyle(descriptionStyle).setCellValue(I18n.getDefault().getMessage(descriptionKey)
                + "\n"
                + I18n.getDefault().getMessage("merlin.word.templating.sheet_configuration_hint"));
        row.setHeight(80).addMergeRegion(0, numberOfColumns - 1);
        return row;
    }

    private ExcelCell addConfigRow(String variable, String value, String descriptionKey) {
        ExcelRow row = sheet.createRow();
        // Variable
        row.createCell().setCellValue(variable);
        // Value
        row.createCell().setCellValue(value);
        // Description
        ExcelCell cell = row.createCell();
        if (descriptionKey != null) {
            cell.setCellValue(I18n.getDefault().getMessage(descriptionKey));
        } else {
            cell.setCellValue("");
        }
        return cell;
    }

    public static String getBooleanAsString(boolean value) {
        return value ? "X" : "";
    }

    private CellStyle createCellStyle(Short color, boolean bold, boolean italic, boolean wrapText, Integer fontSize) {
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

    private void writeValue(ExcelCell cell, Object value, VariableType type) {
        Object targetValue = VariableDefinition.convertValue(value, type);
        if (targetValue == null) {
            return;
        }
        switch (type) {
            case STRING:
                cell.setCellValue((String) targetValue);
                break;
            case INT:
                cell.setCellValue(workbook, (Integer)targetValue);
                break;
            case FLOAT:
                cell.setCellValue(workbook, (Double)targetValue);
                break;
            case DATE:
                log.error("Date not yet implemented.");
        }
    }
}

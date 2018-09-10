package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.excel.ExcelRow;
import de.reinhard.merlin.excel.ExcelSheet;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.stream.Collectors;

public class DefinitionExcelConverter {
    public static ExcelWorkbook writeToWorkbook(TemplateDefinition template) {
        Workbook poiWorkbook = new XSSFWorkbook();
        ExcelWorkbook workbook = new ExcelWorkbook(poiWorkbook);
        CellStyle headRowStyle = workbook.getPOIWorkbook().createCellStyle();
        Font boldFont = workbook.getPOIWorkbook().createFont();
        boldFont.setBold(true);
        headRowStyle.setFont(boldFont);
        CellStyle descriptionStyle = workbook.getPOIWorkbook().createCellStyle();
        Font descriptionFont = workbook.getPOIWorkbook().createFont();
        descriptionFont.setColor(IndexedColors.BLUE_GREY.index);
        descriptionFont.setItalic(true);
        descriptionStyle.setFont(descriptionFont);
        descriptionStyle.setWrapText(true);
        descriptionStyle.setVerticalAlignment(VerticalAlignment.TOP);
        {
            ExcelSheet sheet = workbook.createOrGetSheet("Variables");
            ExcelRow row = sheet.createRow();
            row.createCell().setCellStyle(descriptionStyle).setCellValue(I18n.getDefault().getMessage("merlin.word.templating.sheet_variables_description")
                    + "\n"
            + I18n.getDefault().getMessage("merlin.word.templating.sheet_configuration_hint"));
            row.setHeight(80).addMergeRegion(0, 6);
            row = sheet.createRow();
            row.createCells(headRowStyle, "Variable", "Values", "required", "unique", "type", "Minimum", "Maximum");
            sheet.autosize();
            sheet.setColumnWidth(0, 5000);
            sheet.setColumnWidth(1, 10000);
            for (VariableDefinition variableDefinition : template.getVariableDefinitions()) {
                row = sheet.createRow();
                // Variable
                row.createCell().setCellValue(variableDefinition.getName());
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
                // required
                row.createCell().setCellValue(getBooleanAsString(variableDefinition.isRequired()));
                // unique
                row.createCell().setCellValue(getBooleanAsString(variableDefinition.isUnique()));
                // type
                row.createCell().setCellValue(variableDefinition.getTypeAsString());
                //row.createCell().setCellValue(variableDefinition.getMaximumValue());
                // Minimum
                //row.createCell().setCellValue(variableDefinition.getMaximumValue());
                // Maximum
            }
            sheet.getPoiSheet().setActiveCell(new CellAddress(2,0));
        }
        {
            ExcelSheet sheet = workbook.createOrGetSheet("Dependent Variables");
            ExcelRow row = sheet.createRow();
            row.createCell().setCellStyle(descriptionStyle).setCellValue(I18n.getDefault().getMessage("merlin.word.templating.sheet_dependent_variables_description")
                    + "\n"
                    + I18n.getDefault().getMessage("merlin.word.templating.sheet_configuration_hint"));
            row.setHeight(100).addMergeRegion(0, 3);
            row = sheet.createRow();
            row.createCells(headRowStyle, "Variable", "Depends on variable", "Mapping values", I18n.getDefault().getMessage("merlin.word.templating.sheet_dependent_variables_mapping"));
            sheet.autosize();
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
            sheet.getPoiSheet().setActiveCell(new CellAddress(2,0));
        }
        {
            ExcelSheet sheet = workbook.createOrGetSheet("Configuration");
            ExcelRow row = sheet.createRow();
            row.createCells(headRowStyle, "Variable", "Value", "Description");
            addConfigRow(sheet, template, "Name", template.getName(), "");
            addConfigRow(sheet, template, "Filename", template.getFilenamePattern(), "");
            addConfigRow(sheet, template, "Id", template.getId(), I18n.getDefault().getMessage("merlin.word.templating.please_do_not_modify_id"));
            sheet.autosize();
            sheet.getPoiSheet().setActiveCell(new CellAddress(2,0));
        }
        return workbook;
    }

    private static void addConfigRow(ExcelSheet sheet, TemplateDefinition template, String variable, String value, String description) {
        ExcelRow row = sheet.createRow();
        // Variable
        row.createCell().setCellValue(variable);
        // Value
        row.createCell().setCellValue(value);
        // Description
        row.createCell().setCellValue(description);
    }

    public static String getBooleanAsString(boolean value) {
        return value ? "X" : "";
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

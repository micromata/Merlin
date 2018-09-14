package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.excel.ExcelCell;
import de.reinhard.merlin.excel.ExcelRow;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class TemplateDefinitionExcelWriter extends AbstractExcelWriter {
    private Logger log = LoggerFactory.getLogger(TemplateDefinitionExcelWriter.class);

    private TemplateDefinition template;

    public ExcelWorkbook writeToWorkbook(TemplateDefinition template) {
        this.template = template;
        super.init();
        createVariablesSheet();
        createDependentVariablesSheet();
        createConfigurationSheet();
        addConfigRow("Name", template.getName(), null);
        addConfigRow("Description", template.getDescription(), null);
        addConfigRow("Filename", template.getFilenamePattern(), null);
        ExcelCell cell = addConfigRow("Id", template.getId(), "merlin.word.templating.please_do_not_modify_id");
        cell.setCellStyle(warningCellStyle);
        sheet.autosize();
        sheet.getPoiSheet().setActiveCell(new CellAddress(3, 0));
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
 }

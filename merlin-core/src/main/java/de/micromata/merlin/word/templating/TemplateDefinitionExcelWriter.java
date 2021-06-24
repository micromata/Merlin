package de.micromata.merlin.word.templating;

import de.micromata.merlin.CoreI18n;
import de.micromata.merlin.I18n;
import de.micromata.merlin.excel.ExcelRow;
import de.micromata.merlin.excel.ExcelWorkbook;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class TemplateDefinitionExcelWriter extends AbstractExcelWriter {
    private Logger log = LoggerFactory.getLogger(TemplateDefinitionExcelWriter.class);

    private TemplateDefinition template;
    private I18n i18n;

    public TemplateDefinitionExcelWriter() {
        this.i18n = CoreI18n.getDefault();
    }

    public ExcelWorkbook writeToWorkbook(TemplateDefinition template) {
        return writeToWorkbook(template, true);
    }

    public ExcelWorkbook writeToWorkbook(TemplateDefinition template, boolean writeConfigId) {
        this.template = template;
        super.init();
        createVariablesSheet();
        createDependentVariablesSheet();
        createConfigurationSheet();
        if (writeConfigId) {
            addConfigRow("Id", template.getId(), "merlin.word.templating.definition.config.id");
        }
        addConfigRow("Description", template.getDescription(), "merlin.word.templating.definition.config.description");
        addConfigRow("FilenamePattern", template.getFilenamePattern(), "merlin.word.templating.definition.config.filenamePattern");
        addConfigRow("StronglyRestrictedFilenames", template.isStronglyRestrictedFilenames(), "merlin.word.templating.definition.config.stronglyRestrictedFilenames");
        currentSheet.autosize();
        return workbook;
    }

    private void createVariablesSheet() {
        currentSheet = workbook.createOrGetSheet("Variables");
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_variables_description", 8);
        row = currentSheet.createRow();
        row.createCells(headRowStyle, "Variable", "Type", "required", "unique", "Values", "Minimum", "Maximum", "Description");
        currentSheet.autosize();
        currentSheet.setColumnWidth(0, COLUMN_WIDE_LENGTH);
        currentSheet.setColumnWidth(4, COLUMN_EXTRA_WIDE_LENGTH);
        currentSheet.setColumnWidth(7, COLUMN_EXTRA_WIDE_LENGTH);
        for (VariableDefinition variableDefinition : template.getVariableDefinitions()) {
            row = currentSheet.createRow();
            // Variable
            row.createCell().setCellValue(variableDefinition.getName());
            // type
            row.createCell().setCellValue(variableDefinition.getTypeAsString());
            // required
            row.createCell().setCellValue(templateRunContext.getBooleanAsString(variableDefinition.isRequired()));
            // unique
            row.createCell().setCellValue(templateRunContext.getBooleanAsString(variableDefinition.isUnique()));
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
        currentSheet.getPoiSheet().setActiveCell(new CellAddress(3, 0));
    }

    private void createDependentVariablesSheet() {
        // Dependent variables.
        currentSheet = workbook.createOrGetSheet("Dependent Variables");
        ExcelRow row = addDescriptionRow("merlin.word.templating.sheet_dependent_variables_description", 4);
        row = currentSheet.createRow();
        row.createCells(headRowStyle, "Variable", "Depends on variable", "Mapping values", i18n.getMessage("merlin.word.templating.sheet_dependent_variables_mapping"));
        currentSheet.autosize();
        currentSheet.setColumnWidth(0, COLUMN_WIDE_LENGTH);
        currentSheet.setColumnWidth(1, COLUMN_WIDE_LENGTH);
        currentSheet.setColumnWidth(2, COLUMN_WIDE_LENGTH);
        currentSheet.setColumnWidth(3, COLUMN_EXTRA_WIDE_LENGTH);
        for (DependentVariableDefinition variableDefinition : template.getDependentVariableDefinitions()) {
            row = currentSheet.createRow();
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
            row.createCell().setCellValue(variableDefinition.getMappingInformation(templateRunContext));
        }
        currentSheet.getPoiSheet().setActiveCell(new CellAddress(3, 0));
    }
 }

package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelSheet;
import de.reinhard.merlin.excel.ExcelWorkbook;

import java.util.LinkedList;
import java.util.List;

public class TemplateDefinition {
    private List<VariableDefinition> variableDefinitions = new LinkedList<>();
    private String filenamePattern;

    /**
     *
     * @param variableConfig
     * @return this for chaining.
     */
    public TemplateDefinition add(VariableDefinition variableConfig) {
        variableDefinitions.add(variableConfig);
        return this;
    }

    public void fillExcel(ExcelWorkbook excel) {
        ExcelSheet sheet = excel.createOrGetSheet("Data");
        sheet.cleanSheet();
        //ExcelRow row = sheet.appendRow();

    }

    public String getFilenamePattern() {
        return filenamePattern;
    }

    public void setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;
    }

    public List<VariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }

    public void setVariableDefinitions(List<VariableDefinition> variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
    }
}

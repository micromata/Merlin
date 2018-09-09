package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelSheet;
import de.reinhard.merlin.excel.ExcelWorkbook;

import java.util.LinkedList;
import java.util.List;

public class TemplateDefinition {
    private List<VariableDefinition> variableDefinitions = new LinkedList<>();
    private List<DependentVariableDefinition> dependentVariableDefinitions = new LinkedList<>();
    private String name;
    private String description;
    private String filenamePattern;

    /**
     * @param variableDefinition
     * @return this for chaining.
     */
    public TemplateDefinition add(VariableDefinition variableDefinition) {
        variableDefinitions.add(variableDefinition);
        return this;
    }

    /**
     * @param dependentVariableDefinition
     * @return this for chaining.
     */
    public TemplateDefinition add(DependentVariableDefinition dependentVariableDefinition) {
        dependentVariableDefinitions.add(dependentVariableDefinition);
        return this;
    }

    public void fillExcel(ExcelWorkbook excel) {
        ExcelSheet sheet = excel.createOrGetSheet("Data");
        sheet.cleanSheet();
        //ExcelRow row = sheet.appendRow();

    }

    public String getName() {
        return name;
    }

    public TemplateDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TemplateDefinition setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getFilenamePattern() {
        return filenamePattern;
    }

    public TemplateDefinition setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;        return this;

    }

    public List<VariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }

    public void setVariableDefinitions(List<VariableDefinition> variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
    }

    public List<DependentVariableDefinition> getDependentVariableDefinitions() {
        return dependentVariableDefinitions;
    }

    public void setDependentVariableDefinitions(List<DependentVariableDefinition> dependentVariableDefinitions) {
        this.dependentVariableDefinitions = dependentVariableDefinitions;
    }
}

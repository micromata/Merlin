package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.excel.ExcelSheet;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.*;

/**
 * A template definition defines variabled usable by templates as well as dependent variables.
 */
public class TemplateDefinition {
    private Logger log = LoggerFactory.getLogger(TemplateDefinition.class);
    private List<VariableDefinition> variableDefinitions = new ArrayList<>();
    private List<DependentVariableDefinition> dependentVariableDefinitions = new ArrayList<>();
    private String id;
    private String name;
    private String description;
    private String filenamePattern;
    private FileDescriptor fileDescriptor;

    public TemplateDefinition() {
        this.id = RandomStringUtils.random(20, true, true);
    }

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

    /**
     * @return Unique id (randomized chars). There should not exist multiple templates with the same id.
     */
    public String getId() {
        return id;
    }

    public TemplateDefinition setId(String id) {
        this.id = id;
        return this;
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

    /**
     * @return Filename pattern for generating result files. Important e. g. for serial letters.
     */
    public String getFilenamePattern() {
        return filenamePattern;
    }

    public TemplateDefinition setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;
        return this;

    }

    public FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }

    public void setFileDescriptor(FileDescriptor fileDescriptor) {
        this.fileDescriptor = fileDescriptor;
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

    public VariableDefinition getVariableDefinition(String variableName) {
        return getVariableDefinition(variableName, true);
    }

    /**
     * @param variableName
     * @param logErrors    If true and a variable definition isn't found, an error message will be logged. Default is false.
     * @return
     */
    public VariableDefinition getVariableDefinition(String variableName, boolean logErrors) {
        if (variableDefinitions == null) {
            log.error("Variable named '" + variableName + "' not found in template '" + getName() + "'. No variables defined.");
            return null;
        }
        for (VariableDefinition variableDefinition : variableDefinitions) {
            if (variableName.trim().equals(variableDefinition.getName())) {
                return variableDefinition;
            }
        }
        if (logErrors) {
            log.error("Variable named '" + variableName + "' not found in template '" + getName() + "'.");
        }
        return null;
    }

    /**
     * @return Name of all variables defined (dependant variables included) in a sorted order.
     */
    @Transient
    public List<String> getAllDefinedVariableNames() {
        Set<String> variables = new HashSet<>();
        for (VariableDefinition def : variableDefinitions) {
            variables.add(def.getName());
        }
        for (DependentVariableDefinition def : dependentVariableDefinitions) {
            variables.add(def.getName());
        }
        List<String> result = new ArrayList<>();
        result.addAll(variables);
        Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
        return result;
    }
}

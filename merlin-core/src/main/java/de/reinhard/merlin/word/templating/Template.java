package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.word.Conditionals;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A template refers a template file, optional a template definition file and contains some meta data (such as statistics
 * about variables and their usage).
 */
public class Template {
    private Logger log = LoggerFactory.getLogger(Template.class);
    private List<String> usedVariables = new ArrayList<>();
    private Conditionals conditionals;
    private TemplateDefinition templateDefinition;
    private String directory;
    private String file;
    private Collection<String> allDefinedVariables; // All variables defined in TemplateDefinition.
    private List<String> allUsedVariables;          // All variables used in the Word template.
    private Collection<String> unusedVariables;     // Variables defined but not used in the Word template.
    private Collection<String> undefinedVariables;  // Variables used in the Word template but not defined.

    public List<String> getUsedVariables() {
        return usedVariables;
    }

    public void setUsedVariables(List<String> usedVariables) {
        this.usedVariables = usedVariables;
    }

    public TemplateDefinition getTemplateDefinition() {
        return templateDefinition;
    }

    public void assignTemplateDefinition(TemplateDefinition templateDefinition) {
        this.templateDefinition = templateDefinition;
        this.allDefinedVariables = templateDefinition.getAllDefinedVariableNames();
        if (log.isDebugEnabled()) {
            for (String variable : this.allDefinedVariables) {
                log.debug("Defined variable: " + variable);
            }
        }
        this.unusedVariables = CollectionUtils.subtract(allDefinedVariables, allUsedVariables);
        if (log.isDebugEnabled()) {
            for (String variable : unusedVariables) {
                log.debug("Unused: " + variable);
            }
        }
        this.undefinedVariables = CollectionUtils.subtract(allUsedVariables, allDefinedVariables);
        if (log.isDebugEnabled()) {
            for (String variable : undefinedVariables) {
                log.debug("Undefined: " + variable);
            }
        }
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Conditionals getConditionals() {
        return conditionals;
    }

    public void setConditionals(Conditionals conditionals) {
        this.conditionals = conditionals;
    }

    public Collection<String> getAllDefinedVariables() {
        return allDefinedVariables;
    }

    public void setAllDefinedVariables(Collection<String> allDefinedVariables) {
        this.allDefinedVariables = allDefinedVariables;
    }

    public List<String> getAllUsedVariables() {
        return allUsedVariables;
    }

    public void setAllUsedVariables(List<String> allUsedVariables) {
        this.allUsedVariables = allUsedVariables;
    }

    public Collection<String> getUnusedVariables() {
        return unusedVariables;
    }

    public void setUnusedVariables(Collection<String> unusedVariables) {
        this.unusedVariables = unusedVariables;
    }

    public Collection<String> getUndefinedVariables() {
        return undefinedVariables;
    }

    public void setUndefinedVariables(Collection<String> undefinedVariables) {
        this.undefinedVariables = undefinedVariables;
    }
}

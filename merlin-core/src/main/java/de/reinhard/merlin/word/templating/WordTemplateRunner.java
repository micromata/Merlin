package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.utils.ReplaceUtils;
import de.reinhard.merlin.word.WordDocument;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Runs a template.
 */
public class WordTemplateRunner {
    private Logger log = LoggerFactory.getLogger(WordTemplateRunner.class);
    private TemplateDefinition templateDefinition;
    private WordDocument srcDocument;

    /**
     * @param templateDefinition Bind this Template definition to this Word document. Any template definition read inside the
     *                           Word document will be ignored.
     * @param document
     */
    public WordTemplateRunner(TemplateDefinition templateDefinition, WordDocument document) {
        this.templateDefinition = templateDefinition;
        this.srcDocument = document;
    }

    /**
     * Don't forget to bind the Word template before running {@link #run(Map)}.
     *
     * @param document
     */
    public WordTemplateRunner(WordDocument document) {
        this.srcDocument = document;
    }

    public void setTemplateDefinition(TemplateDefinition templateDefinition) {
        this.templateDefinition = templateDefinition;
    }

    /**
     * Scans the Word file for template definition, such as: <tt>{templateDefinition.refid="Employee contract definition"}</tt>.
     *
     * @return Id of the referenced template definition.
     */
    public String scanForTemplateDefinitionReference() {
        return srcDocument.scanForTemplateDefinitionReference();
    }

    /**
     * Scans the Word file for template id, such as: <tt>{id="Employee contract template"}</tt>.
     *
     * @return Id of this template if given.
     */
    public String scanForTemplateId() {
        return srcDocument.scanForTemplateId();
    }

    public WordDocument run(Map<String, Object> variables) {
        ByteArrayOutputStream bos = srcDocument.getAsByteArrayOutputStream();
        WordDocument newDocument = new WordDocument(bos.toInputStream(), srcDocument.getFilename());
        newDocument.setFilename(srcDocument.getFilename());
        if (templateDefinition != null && templateDefinition.getDependentVariableDefinitions() != null) {
            for (DependentVariableDefinition depVar : templateDefinition.getDependentVariableDefinitions()) {
                variables.put(depVar.getName(), depVar.getMappedValue(variables));
            }
        }
        newDocument.process(variables);
        return newDocument;
    }

    /**
     * Creates a filename from the file pattern of the template definition (if exist). Otherwise the file name will
     * be the same as the given default filename with suffix '-generated'.
     *
     * @param defaultFilenamePattern Default filename if not file pattern is found in template definition.
     * @param variables              Needed for replacing variable inside the filename.
     * @return File name, Characters not matching letters, digits and {_-.@} are replaced by underscore
     * (also white spaces are replaced).
     */
    public String createFilename(String defaultFilenamePattern, Map<String, Object> variables) {
        return createFilename(defaultFilenamePattern, variables, true);
    }

    /**
     * Creates a filename from the file pattern of the template definition (if exist). Otherwise the file name will
     * be the same as the given default filename with suffix '-generated'.
     *
     * @param defaultFilenamePattern       Default filename pattern if no file pattern is found in template definition.
     * @param variables                    Needed for replacing variable inside the filename.
     * @param useTemplateDefinitionPattern If true, the pattern of the template definition will be used (if given), otherwise the defaultFilenamePattern will be used. Default is true.
     * @return File name, Characters not matching letters, digits and {_-.@} are replaced by underscore
     * (also white spaces are replaced).
     */
    public String createFilename(String defaultFilenamePattern, Map<String, Object> variables, boolean useTemplateDefinitionPattern) {
        String pattern = defaultFilenamePattern;
        if (useTemplateDefinitionPattern && templateDefinition != null) {
            pattern = FilenameUtils.getBaseName(templateDefinition.getFilenamePattern());
        }
        if (StringUtils.isNotBlank(pattern)) {
            String filename = ReplaceUtils.replace(pattern, variables);
            boolean stronglyRestrictedFilenames = templateDefinition != null ? templateDefinition.isStronglyRestrictedFilenames() : true;
            return ReplaceUtils.encodeFilename(filename, stronglyRestrictedFilenames) + ".docx";
        }
        return FilenameUtils.getBaseName(defaultFilenamePattern) + "-generated." + FilenameUtils.getExtension(defaultFilenamePattern);
    }
}

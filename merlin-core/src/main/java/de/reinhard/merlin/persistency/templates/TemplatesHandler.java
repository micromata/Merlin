package de.reinhard.merlin.persistency.templates;

import de.reinhard.merlin.persistency.DirectoryWatchEntry;
import de.reinhard.merlin.persistency.FileDescriptor;
import de.reinhard.merlin.word.WordDocument;
import de.reinhard.merlin.word.templating.Template;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.WordTemplateChecker;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

class TemplatesHandler extends AbstractHandler<Template> {
    private Logger log = LoggerFactory.getLogger(TemplatesHandler.class);

    TemplatesHandler(DirectoryScanner directoryScanner) {
        super(directoryScanner, "Template");
        this.supportedFileExtensions = new String[]{"docx"};
    }

    @Override
    Template read(DirectoryWatchEntry watchEntry, Path path, FileDescriptor fileDescriptor) {
        WordDocument doc = WordDocument.create(path);
        WordTemplateChecker templateChecker = new WordTemplateChecker(doc);
        if (CollectionUtils.isEmpty(templateChecker.getTemplate().getStatistics().getAllUsedVariables())) {
            log.debug("Skipping Word document: '" + path.toAbsolutePath()
                    + "'. It's seemd to be not a Merlin template. No variables and conditionals found.");
            return null;
        }
        templateChecker.getTemplate().setFileDescriptor(fileDescriptor);
        String templateDefinitionId = doc.scanForTemplateDefinitionReference();
        if (templateDefinitionId != null) {
            log.debug("Template definition reference found: " + templateDefinitionId);
            TemplateDefinition templateDefinition = directoryScanner._getTemplateDefinition(templateDefinitionId);
            if (templateDefinition != null) {
                templateChecker.getTemplate().assignTemplateDefinition(templateDefinition);
            } else {
                log.warn("Template definition not found: " + templateDefinitionId);
            }
        } else {
            TemplateDefinition templateDefinition = directoryScanner._getTemplateDefinition(templateChecker.getTemplate().getFileDescriptor());
            if (templateDefinition != null) {
                templateChecker.getTemplate().assignTemplateDefinition(templateDefinition);
                log.info("Found matching template definition: " + templateDefinition.getFileDescriptor());
            }
        }
        return templateChecker.getTemplate();
    }

    @Override
    Template getItem(FileDescriptor fileDescriptor) {
        return directoryScanner._getTemplate(fileDescriptor);
    }
}

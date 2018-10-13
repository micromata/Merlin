package de.reinhard.merlin.persistency.templates;

import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.persistency.DirectoryWatchEntry;
import de.reinhard.merlin.persistency.FileDescriptor;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.TemplateDefinitionExcelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

class TemplateDefinitionsHandler extends AbstractHandler<TemplateDefinition> {
    private Logger log = LoggerFactory.getLogger(TemplateDefinitionsHandler.class);

    TemplateDefinitionsHandler(DirectoryScanner directoryScanner) {
        super(directoryScanner, "TemplateDefinition");
        this.supportedFileExtensions = new String[]{"xlsx", "xls"};
    }

    @Override
    TemplateDefinition read(DirectoryWatchEntry watchEntry, Path path, FileDescriptor fileDescriptor) {
        ExcelWorkbook workbook;
        try {
            workbook = ExcelWorkbook.create(path);
        } catch (Exception ex) {
            log.info("Ignoring unsupported file: " + path);
            return null;
        }
        TemplateDefinitionExcelReader templateReader = new TemplateDefinitionExcelReader();
        if (!templateReader.isMerlinTemplateDefinition(workbook)) {
            return null;
        }
        TemplateDefinition templateDefinition = templateReader.readFromWorkbook(workbook, false);
        if (templateDefinition == null) {
            return null;
        }
        if (!templateReader.isValidTemplateDefinition()) {
            log.warn("Merlin template definition isn't valid for '" + templateDefinition.getId() + "': " + path);
        }
        return templateDefinition;
    }

    /**
     *
     * @param id Id or primary key of the template definition.
     * @return
     */
    TemplateDefinition getTemplateDefinition(String id) {
        String search = id.trim().toLowerCase();
        for (TemplateDefinition templateDefinition : getItems()) {
            if (id.equals(templateDefinition.getFileDescriptor().getPrimaryKey())) {
                return templateDefinition;
            }
            if (search.equals(templateDefinition.getId().trim().toLowerCase())) {
                return templateDefinition;
            }
        }
        return null;
    }
}

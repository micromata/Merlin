package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.utils.Converter;
import de.reinhard.merlin.utils.MerlinFileUtils;
import de.reinhard.merlin.utils.ZipUtils;
import de.reinhard.merlin.word.WordDocument;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialTemplateRunner {
    private static Logger log = LoggerFactory.getLogger(SerialTemplateRunner.class);

    private SerialData serialData;
    private WordDocument templateDocument;
    private String zipFilename;

    public SerialTemplateRunner(SerialData serialData, WordDocument templateDocument) {
        this.serialData = serialData;
        this.templateDocument = templateDocument;
    }

    /**
     *
     * @param filename Filename of e. g. Serial template xls file. Used for creating name of zip archive based on this file.
     * @return
     */
    public byte[] run(String filename) {
        WordTemplateChecker checker = new WordTemplateChecker(templateDocument);
        serialData.setTemplate(checker.getTemplate());
        TemplateDefinition templateDefinition = serialData.getTemplateDefinition();
        Template template = serialData.getTemplate();
        String tdString = templateDefinition != null ? templateDefinition.getId() : "-";
        log.info("Processing serial templates for template=" + template.getDisplayName() + ", template definition=" +
                tdString);
        this.zipFilename = MerlinFileUtils.getISODate() + "_" + FilenameUtils.getBaseName(filename) + ".zip";
        ZipUtils zipUtil = new ZipUtils(zipFilename);
        int counter = 0;
        int maxEntries = serialData.getEntries().size();
        for (SerialDataEntry entry : serialData.getEntries()) {
            WordTemplateRunner runner = new WordTemplateRunner(templateDefinition, templateDocument);
            WordDocument result = runner.run(entry.getVariables());
            entry.getVariables().put("counter", Converter.formatNumber(++counter, maxEntries));
            String zipEntryFilename = runner.createFilename(serialData.getFilenamePattern(), entry.getVariables(), false);
            zipUtil.addZipEntry(zipEntryFilename, result.getAsByteArrayOutputStream().toByteArray());
            log.info("Generating serial template entry: " + zipEntryFilename);
        }
        byte[] zipByteArray = zipUtil.closeAndGetByteArray();
        return zipByteArray;
    }

    public String getZipFilename() {
        return zipFilename;
    }
}

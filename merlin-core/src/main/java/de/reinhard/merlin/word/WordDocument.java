package de.reinhard.merlin.word;

import de.reinhard.merlin.persistency.PersistencyRegistry;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordDocument implements AutoCloseable {
    private Logger log = LoggerFactory.getLogger(WordDocument.class);
    XWPFDocument document;
    private String filename;
    private File file;
    private InputStream inputStream;

    public static WordDocument create(Path path) {
        File file = PersistencyRegistry.getDefault().getFile(path);
        if (file != null) {
            return new WordDocument(file);
        }
        String filename = path.getFileName().toString();
        return new WordDocument(PersistencyRegistry.getDefault().getInputStream(path), filename);
    }

    /**
     * @param document
     */
    public WordDocument(XWPFDocument document) {
        this.document = document;
    }

    /**
     * @param filename File to read document from.
     */
    public WordDocument(String filename) {
        this(new File(filename));
    }

    public WordDocument(File wordFile) {
        this.file = wordFile;
        this.filename = wordFile.getAbsolutePath();
        try {
            document = new XWPFDocument(OPCPackage.open(filename));
        } catch (IOException ex) {
            log.error("Couldn't open File '" + filename + "': " + ex.getMessage());
            throw new RuntimeException(ex);
        } catch (InvalidFormatException ex) {
            log.error("Unsupported file format '" + filename + "': " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param inputStream
     * @param filename    Only for logging purposes if any error occurs.
     */
    public WordDocument(InputStream inputStream, String filename) {
        this.inputStream = inputStream;
        this.filename = filename;
        try {
            document = new XWPFDocument(inputStream);
        } catch (IOException ex) {
            log.error("Couldn't open File '" + filename + "' from IntputStream: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public XWPFDocument getDocument() {
        return document;
    }

    public void process(Map<String, Object> variables) {
        Conditionals conditionals = getConditionals();
        conditionals.process(variables);
        replaceVariables(variables);
    }

    public String scanForTemplateDefinitionReference() {
        String ref = (String) processAllParagraphs(new RunsProcessorExecutor() {
            @Override
            Object process(RunsProcessor processor, Object param) {
                return processor.scanForTemplateDefinitionReference();
            }
        }, null);
        return ref;
    }

    public String scanForTemplateId() {
        String id = (String) processAllParagraphs(new RunsProcessorExecutor() {
            @Override
            Object process(RunsProcessor processor, Object param) {
                return processor.scanForTemplateId();
            }
        }, null);
        return id;
    }

    public Conditionals getConditionals() {
        Conditionals conditionals = new Conditionals(this);
        conditionals.read();
        return conditionals;
    }

    public Set<String> getVariables() {
        Set<String> variables = new HashSet<>();
        processAllParagraphs(new RunsProcessorExecutor() {
            @Override
            Object process(RunsProcessor processor, Object param) {
                processor.scanVariables((Set<String>) param);
                return null;
            }
        }, variables);
        return variables;
    }

    public ByteArrayOutputStream getAsByteArrayOutputStream() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            document.write(bos);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        return bos;
    }

    private void replaceVariables(Map<String, Object> variables) {
        processAllParagraphs(new RunsProcessorExecutor() {
            @Override
            Object process(RunsProcessor processor, Object param) {
                processor.replace((Map<String, ?>) param);
                return null;
            }
        }, variables);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getLength() {
        if (this.file != null) {
            return file.length();
        }
        return null;
    }

    private Object processAllParagraphs(RunsProcessorExecutor processor, Object param) {
        for (IBodyElement element : document.getBodyElements()) {
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                Object result = processor.process(new RunsProcessor(paragraph), param);
                if (result != null) {
                    return result;
                }
            } else if (element instanceof XWPFTable) {
                XWPFTable table = (XWPFTable) element;
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            Object result = processor.process(new RunsProcessor(paragraph), param);
                            if (result != null) {
                                return result;
                            }
                        }
                    }
                }
            } else if (element instanceof XWPFSDT) {
                ISDTContent content = ((XWPFSDT) element).getContent();
                if (content == null) {
                    log.info("Unsupported XWPFSDT element (not body or no paragraphs).");
                } else {
                    log.warn("Unsupported content: " + content.getText());
                }
            } else {
                log.warn("Unsupported body element: " + element);
            }
        }
        return null;
    }

    abstract class RunsProcessorExecutor {
        abstract Object process(RunsProcessor processor, Object param);
    }
}

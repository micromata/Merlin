package de.reinhard.merlin.word;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class WordDocument {
    private Logger log = LoggerFactory.getLogger(WordDocument.class);
    XWPFDocument document;
    private String filename;

    /**
     * @param document
     */
    public WordDocument(XWPFDocument document) {
        this.document = document;
    }

    /**
     *
     * @param filename File to read document from.
     */
    public WordDocument(String filename) {
        this(new File(filename));
    }

    public WordDocument(File wordFile) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(wordFile);
        } catch (FileNotFoundException ex) {
            log.error("Couldn't open File '" + wordFile.getAbsolutePath() + "': ", ex);
            throw new RuntimeException(ex);
        }
        this.filename = wordFile.getAbsolutePath();
        init(inputStream);
    }

    /**
     * @param inputStream
     */
    public WordDocument(InputStream inputStream) {
        init(inputStream);
    }

    private void init(InputStream inputStream) {
        try {
            document = new XWPFDocument(OPCPackage.open(inputStream));
        } catch (IOException ex) {
            log.error("Couldn't open File '" + filename + "': " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } catch (InvalidFormatException ex) {
            log.error("Unsupported file format '" + filename + "': " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public XWPFDocument getDocument() {
        return document;
    }

    public void process(Map<String, String> variables) {
        Conditionals conditionals = new Conditionals(this);
        conditionals.read();
        conditionals.process(variables);
        replaceVariables(variables);
    }

    private void replaceVariables(Map<String, String> variables) {
        for (IBodyElement element : document.getBodyElements()) {
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                new RunsProcessor(paragraph).replace(variables);
            } else if (element instanceof XWPFTable) {
                XWPFTable table = (XWPFTable) element;
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            new RunsProcessor(p).replace(variables);
                        }
                    }
                }
            } else {
                log.warn("Unsupported body element: " + element);
            }
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

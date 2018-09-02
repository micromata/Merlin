package de.reinhard.merlin.word;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WordDocument {
    private Logger log = LoggerFactory.getLogger(WordDocument.class);
    XWPFDocument document;

    public WordDocument(XWPFDocument document) {
        this.document = document;
    }

    public WordDocument(String wordFilename) {
        this(new File(wordFilename));
    }

    public WordDocument(File wordFile) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(wordFile);
        } catch (FileNotFoundException ex) {
            log.error("Couldn't open File '" + wordFile.getAbsolutePath() + "': ", ex);
            throw new RuntimeException(ex);
        }
        try {
            document = new XWPFDocument(OPCPackage.open(inputStream));
        } catch (IOException ex) {
            log.error("Couldn't open File '" + wordFile.getAbsolutePath() + "': " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        } catch (InvalidFormatException ex) {
            log.error("Unsupported file format '" + wordFile.getAbsolutePath() + "': " + ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public XWPFDocument getDocument() {
        return document;
    }

    public void process(Map<String, String> variables) {
        Conditionals conditionals = new Conditionals();
        conditionals.read(document.getBodyElements());
        replaceVariables(variables);
    }

    private void replaceVariables(Map<String, String> variables) {
        for (IBodyElement element : document.getBodyElements()) {
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    replace(runs, variables);
                }
            } else if (element instanceof XWPFTable) {
                XWPFTable table = (XWPFTable) element;
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            replace(p.getRuns(), variables);
                        }
                    }
                }
            } else {
                log.warn("Unsupported body element: " + element);
            }
        }
    }

    private void replace(List<XWPFRun> runs, Map<String, String> variables) {
        RunsProcessor processor = new RunsProcessor(runs);
        processor.replace(variables);
    }
}

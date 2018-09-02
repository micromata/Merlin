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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordDocument {
    private Logger log = LoggerFactory.getLogger(WordDocument.class);
    static final String MAGIC_MARK_TO_REMOVE = "{{REMOVE_THIS_XWPFParagraph}}";

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
        Conditionals conditionals = new Conditionals(this);
        conditionals.read();
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

    void markParagraphToRemove(XWPFParagraph paragraph) {
        for (XWPFParagraph par : document.getParagraphs()) {
            if (par == paragraph) {
                List<XWPFRun> runs = par.getRuns();
                if (runs != null && runs.size() > 0) {
                    runs.get(0).setText(MAGIC_MARK_TO_REMOVE, 0);
                }
                return;
            }
        }
        log.error("Paragraph not found to remove: " + paragraph.getText());
    }

    void removeMarkedParagraphs() {

        List<XWPFParagraph> paragraphs =  document.getParagraphs();
        for (int i = paragraphs.size() - 1; i >= 0; i--) {
            XWPFParagraph par = paragraphs.get(i);
            if (par.getRuns() == null || par.getRuns().size() == 0) {
                continue;
            }
            if (MAGIC_MARK_TO_REMOVE.equals(par.getRuns().get(0).getText(0))) {
                document.removeBodyElement(i);
            }
        }

    }

    private void replace(List<XWPFRun> runs, Map<String, String> variables) {
        RunsProcessor processor = new RunsProcessor(runs);
        processor.replace(variables);
    }
}

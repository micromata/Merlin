package de.reinhard.merlin.word;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class MSWord {
    private Logger log = LoggerFactory.getLogger(MSWord.class);
    XWPFDocument document;

    public MSWord(String wordFilename) {
        this(new File(wordFilename));
    }

    public MSWord(File wordFile) {
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
        replaceVariables(variables);
    }

    private void processConditionals(Map<String, String> variables) {

    }

    private void replaceVariables(Map<String, String> variables) {
        for (XWPFParagraph p : document.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                replace(runs, variables);
            }
        }
        for (XWPFTable tbl : document.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replace(p.getRuns(), variables);
                    }
                }
            }
        }
    }

    private void replace(List<XWPFRun> runs, Map<String, String> variables) {
        RunsParser parser = new RunsParser(runs, variables);
        parser.run();
    }
}

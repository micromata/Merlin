package de.reinhard.merlin.word;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                for (XWPFRun run : runs) {
                    replace(run, variables);
                }
            }
        }
        for (XWPFTable tbl : document.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun run : p.getRuns()) {
                            replace(run, variables);
                        }
                    }
                }
            }
        }
    }

    private void replace(XWPFRun run, Map<String, String> variables) {
        String text = run.getText(0);
        log.debug(text);
        if (text == null) {
            return;
        }
        Pattern pattern = Pattern.compile("#(\\w*)#");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        boolean found = false;
        while (matcher.find()) {
            found = true;
            String group = matcher.group(1);
            String value = variables.get(group);
            if (value != null) {
                matcher.appendReplacement(sb, value);
            } else {
                matcher.appendReplacement(sb, "#" + group + "#");
            }
        }
        if (found) {
            matcher.appendTail(sb);
            run.setText(sb.toString(), 0);
        }
    }
}

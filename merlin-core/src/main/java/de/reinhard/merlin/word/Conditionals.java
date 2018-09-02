package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;

public class Conditionals {
    private Logger log = LoggerFactory.getLogger(Conditionals.class);
    private SortedSet<Conditional> conditionals;


    void read(List<IBodyElement> elements) {
        conditionals = new TreeSet<>();
        int bodyElementCounter = 0;
        for (IBodyElement element : elements) {
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    read(runs, bodyElementCounter);
                }
            }
            ++bodyElementCounter;
        }
        if (log.isDebugEnabled()) {
            log.debug("Conditionals:");
            for (Conditional conditional : conditionals) {
                log.debug("Conditional: " + conditional);
            }
        }
    }

    private void read(List<XWPFRun> runs, int bodyElementNumber) {
        RunsProcessor processor = new RunsProcessor(runs);
        String text = processor.getText();
        Matcher beginMatcher = Conditional.beginIfPattern.matcher(text);
        while (beginMatcher.find()) {
            Conditional conditional = new Conditional(beginMatcher, bodyElementNumber, processor);
            conditionals.add(conditional);
        }
        Matcher endMatcher = Conditional.endIfPattern.matcher(text);
        while (endMatcher.find()) {
            DocumentPosition endifPosition = processor.getRunIdxAndPosition(endMatcher.start());
            Conditional conditional = findMatchingConditional(bodyElementNumber, endifPosition);
            if (conditional == null) {
                log.error("No conditional found for {endif} expression: " + processor.getText());
                continue;
            }
            if (conditional.getEndEndif() != null) {
                log.error("Multiple endif found for {endif} expression: " + processor.getText());
                continue;
            }
            conditional.setEndif(bodyElementNumber, endMatcher, processor);
        }

        // Detect hierarchy:
        for (Conditional conditional : conditionals) {
            Conditional parent = findMatchingConditional(conditional.getBodyElementNumber(), conditional.getStartIfExpression());
            if (parent != null) {
                if (parent == conditional) {
                    log.error("Internal error, shouldn't occur. Found parent equals conditional itself.");
                    continue;
                }
                conditional.setParent(parent);
                if (log.isDebugEnabled()) {
                    log.debug("Parent if-statement found for " + conditional + ": parent=" + parent);
                }
            }
        }
    }

    private Conditional findMatchingConditional(int bodyElementNumber, DocumentPosition position) {
        Conditional last = null;
        for (Conditional conditional : conditionals) {
            if (bodyElementNumber < conditional.getBodyElementNumber()) {
                return last; // return last element, because current is after requested position.
            }
            if (bodyElementNumber < conditional.getEndifBodyElementNumber()) {
                // Endif already passed.
                return last;
            }
            if (bodyElementNumber == conditional.getBodyElementNumber()) {
                // Check the position inside the bodyElement.
                if (position.compareTo(conditional.getEndIfExpression()) < 0) {
                    return last;
                }
            }
            last = conditional;
        }
        return last;
    }

    public SortedSet<Conditional> getConditionals() {
        return conditionals;
    }
}
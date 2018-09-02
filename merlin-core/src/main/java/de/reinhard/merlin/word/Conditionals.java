package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;

public class Conditionals {
    private Logger log = LoggerFactory.getLogger(Conditionals.class);
    private SortedSet<Conditional> conditionals;

    /**
     * Parses all conditionals and build also a conditional tree.
     *
     * @param elements
     */
    void read(List<IBodyElement> elements) {
        conditionals = new TreeSet<>();
        SortedSet<DocumentRange> allControls = new TreeSet<>();
        Map<DocumentRange, Conditional> conditionalMap = new HashMap<>();
        int bodyElementCounter = 0;
        for (IBodyElement element : elements) {
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    read(runs, bodyElementCounter, allControls, conditionalMap);
                }
            }
            ++bodyElementCounter;
        }
        Conditional current = null;
        for (DocumentRange range : allControls) {
            Conditional conditional = conditionalMap.get(range);
            if (conditional != null) {
                log.debug("Processing conditional: " + conditional);
                // If-expression:
                if (current != null) {
                    // This is a child if-expression of current.
                    conditional.setParent(current);
                }
                current = conditional; // Set child as current.
            } else {
                log.debug("Processing endif: " + range);
                // endif-expression:
                if (current == null) {
                    log.error("endif without if-expression found. Ignoring it.");
                } else {
                    current.setEndif(range);
                    current = current.getParent(); // May-be null.
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Conditionals:");
            for (Conditional conditional : conditionals) {
                log.debug("Conditional: " + conditional);
            }
        }
    }

    void process(List<IBodyElement> elements) {
        int bodyElementCounter = 0;
        for (IBodyElement element : elements) {
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null) {
                    process(runs, bodyElementCounter);
                }
            }
            ++bodyElementCounter;
        }
    }

    Conditional getConditional(DocumentPosition position) {
        for (Conditional conditional : conditionals) {
        }
        return null;
    }

    private void read(List<XWPFRun> runs, int bodyElementNumber, SortedSet<DocumentRange> allControls,
                      Map<DocumentRange, Conditional> conditionalMap) {
        RunsProcessor processor = new RunsProcessor(runs);
        String text = processor.getText();
        Matcher beginMatcher = Conditional.beginIfPattern.matcher(text);
        while (beginMatcher.find()) {
            Conditional conditional = new Conditional(beginMatcher, bodyElementNumber, processor);
            conditionals.add(conditional);
            allControls.add(conditional.getIfExpressionRange());
            conditionalMap.put(conditional.getIfExpressionRange(), conditional);
        }
        Matcher endMatcher = Conditional.endIfPattern.matcher(text);
        while (endMatcher.find()) {
            DocumentPosition endifStart = processor.getRunIdxAndPosition(bodyElementNumber, endMatcher.start());
            DocumentPosition endifEnd = processor.getRunIdxAndPosition(bodyElementNumber, endMatcher.end());
            DocumentRange range = new DocumentRange(endifStart, endifEnd);
            allControls.add(range);
        }
    }

    private void process(List<XWPFRun> runs, int bodyElementNumber) {
        RunsProcessor processor = new RunsProcessor(runs);
        String text = processor.getText();

    }

    public SortedSet<Conditional> getConditionals() {
        return conditionals;
    }
}
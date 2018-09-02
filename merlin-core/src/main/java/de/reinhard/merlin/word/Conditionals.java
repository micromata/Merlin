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
            RunsProcessor.Position endifPosition = processor.getRunIdxAndPosition(endMatcher.start());
            Conditional conditional = findMatchingConditional(bodyElementNumber, endifPosition);
            conditional.setEndif(endMatcher, processor);
        }
        //return processor.processConditionals(hidden);
    }

    private Conditional findMatchingConditional(int bodyElementNumber, RunsProcessor.Position position) {
        Conditional last = null;
        for (Conditional conditional : conditionals) {
            if (bodyElementNumber < conditional.getBodyElementNumber()) {
                return last; // return last element, because current is after requested position.
            }
            if (bodyElementNumber == conditional.getBodyElementNumber()) {
                // Check the position inside the bodyElement.
                if (position.compareTo(conditional.getEndEndif()) < 0) {
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
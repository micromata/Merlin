package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;

public class Conditionals {
    private SortedSet<Conditional> set;


    void read(List<IBodyElement> elements) {
        set = new TreeSet<>();
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

    }

    private void read(List<XWPFRun> runs, int bodyElementNumber) {
        RunsProcessor processor = new RunsProcessor(runs);
        String text = processor.getText();
        Matcher beginMatcher = Conditional.beginIfPattern.matcher(text);
        while (beginMatcher.find()) {
            Conditional conditional = new Conditional(beginMatcher, bodyElementNumber, processor);
            set.add(conditional);
        }
        Matcher endMatcher = Conditional.endIfPattern.matcher(text);
        while (endMatcher.find()) {

        }
        //return processor.processConditionals(hidden);
    }

}

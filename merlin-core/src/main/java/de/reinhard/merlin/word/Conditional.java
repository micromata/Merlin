package de.reinhard.merlin.word;

import de.reinhard.merlin.csv.CSVStringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Conditional implements Comparable<Conditional> {
    private static Logger log = LoggerFactory.getLogger(Conditional.class);

    static Pattern beginIfPattern = Pattern.compile("\\{if\\s+(" + RunsProcessor.IDENTIFIER_REGEXP + ")\\s*(!?=|!?\\s*in)\\s*(.*)\\s*\\}");
    static Pattern endIfPattern = Pattern.compile("\\{endif\\}");


    private XWPFParagraph paragraph;
    private Conditional parent;
    private int bodyElementNumber;
    private RunsProcessor.Position startExpression, endExpression; // The range of the if-statement expression (as to be removed from the doc).
    private int endifBodyElementNumber;
    private RunsProcessor.Position startEndif, endEndif; // The range of the endif expression (as to be removed from the doc).
    private String variable;
    private String[] values;
    private ConditionalType type = ConditionalType.EQUAL;

    Conditional(Matcher matcher, int bodyElementNumber, RunsProcessor processor) {
        this.bodyElementNumber = bodyElementNumber;
        variable = matcher.group(1);
        String str = matcher.group(2);
        if (str != null) {
            if ("!=".equals(str)) {
                type = ConditionalType.NOT_EQUAL;
            } else if ("in".equals(str)) {
                type = ConditionalType.IN;
            } else if ("!in".equals(str)) {
                type = ConditionalType.NOT_IN;
            }
        }
        values = CSVStringUtils.parseStringList(matcher.group(3));
        startExpression = processor.getRunIdxAndPosition(matcher.start());
        endExpression = processor.getRunIdxAndPosition(matcher.end());
    }

    void setEndif(Matcher matcher, RunsProcessor processor) {
        startEndif = processor.getRunIdxAndPosition(matcher.start());
        endEndif = processor.getRunIdxAndPosition(matcher.end());
    }

    boolean documentPartVisible() {
        return true;
    }

    public String getVariable() {
        return variable;
    }

    public String[] getValues() {
        return values;
    }

    public int getBodyElementNumber() {
        return bodyElementNumber;
    }

    public RunsProcessor.Position getEndEndif() {
        return endEndif;
    }

    @Override
    public int compareTo(Conditional o) {
        return new CompareToBuilder()
                .append(bodyElementNumber, o.bodyElementNumber)
                .append(startExpression, o.startExpression).toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("variable", variable)
                .append("type", type)
                .append("Values", values)
                .append("if-no", bodyElementNumber)
                .append("if-pos", startExpression)
                .append("endif-no", bodyElementNumber)
                .append("endif-pos", startExpression)
                .toString();
    }
}

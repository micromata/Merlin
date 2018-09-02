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

    static Pattern beginIfPattern = Pattern.compile("\\{if\\s+(" + RunsProcessor.IDENTIFIER_REGEXP + ")\\s*(!?=|!?\\s*in)\\s*([^\\}]*)\\s*\\}");
    static Pattern endIfPattern = Pattern.compile("\\{endif\\}");


    private XWPFParagraph paragraph;
    private Conditional parent;
    private int bodyElementNumber;
    private DocumentPosition startIfExpression, endIfExpression; // The range of the if-statement expression (as to be removed from the doc).
    private int endifBodyElementNumber = -1;
    private DocumentPosition startEndif, endEndif; // The range of the endif expression (as to be removed from the doc).
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
        startIfExpression = processor.getRunIdxAndPosition(matcher.start());
        endIfExpression = processor.getRunIdxAndPosition(matcher.end());
    }

    void setEndif(int endifBodyElementNumber, Matcher matcher, RunsProcessor processor) {
        this.endifBodyElementNumber = endifBodyElementNumber;
        startEndif = processor.getRunIdxAndPosition(matcher.start());
        endEndif = processor.getRunIdxAndPosition(matcher.end());
    }

    boolean documentPartVisible() {
        return true;
    }

    public String getVariable() {
        return variable;
    }

    public ConditionalType getType() {
        return type;
    }

    public String[] getValues() {
        return values;
    }

    public int getBodyElementNumber() {
        return bodyElementNumber;
    }

    public DocumentPosition getStartIfExpression() {
        return startIfExpression;
    }

    public DocumentPosition getEndIfExpression() {
        return endIfExpression;
    }

    public int getEndifBodyElementNumber() {
        return endifBodyElementNumber;
    }

    public DocumentPosition getEndEndif() {
        return endEndif;
    }

    public Conditional getParent() {
        return parent;
    }

    public void setParent(Conditional parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(Conditional o) {
        return new CompareToBuilder()
                .append(bodyElementNumber, o.bodyElementNumber)
                .append(startIfExpression, o.startIfExpression).toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("variable", variable)
                .append("type", type)
                .append("Values", values)
                .append("if-no", bodyElementNumber)
                .append("if-pos", startIfExpression)
                .append("endif-no", bodyElementNumber)
                .append("endif-pos", startIfExpression)
                .toString();
    }
}

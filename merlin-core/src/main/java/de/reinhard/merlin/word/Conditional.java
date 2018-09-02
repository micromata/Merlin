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
    private DocumentRange ifExpressionRange, endifExpressionRange; // range of the expression itselves.
    private DocumentRange conditionalRange; // range between if- and endif-statement.
    private String variable;
    private String[] values;
    private ConditionalType type = ConditionalType.EQUAL;

    Conditional(Matcher matcher, int bodyElementNumber, RunsProcessor processor) {
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
        ifExpressionRange = new DocumentRange(processor.getRunIdxAndPosition(bodyElementNumber, matcher.start()),
                processor.getRunIdxAndPosition(bodyElementNumber, matcher.end()));
    }

    void setEndif(Matcher matcher, int endifBodyElementNumber, RunsProcessor processor) {
        endifExpressionRange = new DocumentRange(processor.getRunIdxAndPosition(endifBodyElementNumber, matcher.start()),
                processor.getRunIdxAndPosition(endifBodyElementNumber, matcher.end()));
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

    public Conditional getParent() {
        return parent;
    }

    public void setParent(Conditional parent) {
        this.parent = parent;
    }

    public DocumentRange getIfExpressionRange() {
        return ifExpressionRange;
    }

    public DocumentRange getEndifExpressionRange() {
        return endifExpressionRange;
    }

    @Override
    public int compareTo(Conditional o) {
        return new CompareToBuilder()
                .append(ifExpressionRange.getStartPosition(), o.ifExpressionRange.getStartPosition()).toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("variable", variable)
                .append("type", type)
                .append("Values", values)
                .append("if-pos", ifExpressionRange)
                .append("endif-pos", endifExpressionRange)
                .toString();
    }
}

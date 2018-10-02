package de.reinhard.merlin.word;

import de.reinhard.merlin.utils.ReplaceUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractConditional implements Comparable<AbstractConditional> {
    private static Logger log = LoggerFactory.getLogger(AbstractConditional.class);

    private XWPFParagraph paragraph;
    protected AbstractConditional parent;
    private DocumentRange conditionalExpressionRange, endConditionalExpressionRange; // range of the expression itselves.
    private DocumentRange range; // range between if- and endif-statement.
    private List<AbstractConditional> childConditionals;
    private String conditionalStatement;
    protected String variable;
    protected ConditionalType type;

    static Pattern beginIfPattern = Pattern.compile("\\{if\\s+(" + ReplaceUtils.IDENTIFIER_REGEXP + ")\\s*(!?=|!?\\s*in|<=?|>=?)\\s*([^\\}]*)\\s*\\}");
    static Pattern notInComparatorPattern = Pattern.compile("!?\\s*in");
    static Pattern endIfPattern = Pattern.compile("\\{endif\\}");

    public static AbstractConditional createConditional(Matcher matcher, int bodyElementNumber, RunsProcessor processor) {
        String str = matcher.group(2);
        ConditionalType type = ConditionalType.EQUAL;
        if (str != null) {
            if ("!=".equals(str)) {
                type = ConditionalType.NOT_EQUAL;
            } else if ("in".equals(str)) {
                type = ConditionalType.IN;
            } else if (notInComparatorPattern.matcher(str).matches()) {
                type = ConditionalType.NOT_IN;
            } else if ("<".equals(str)) {
                type = ConditionalType.LESS;
            } else if ("<=".equals(str)) {
                type = ConditionalType.LESS_EQUAL;
            } else if (">".equals(str)) {
                type = ConditionalType.GREATER;
            } else if (">=".equals(str)) {
                type = ConditionalType.GREATER_EQUAL;
            }
        }
        AbstractConditional conditional;
        if (type.isIn(ConditionalType.EQUAL, ConditionalType.NOT_EQUAL, ConditionalType.IN, ConditionalType.NOT_IN)) {
            conditional = new ConditionalString(matcher, bodyElementNumber, processor);
        } else {
            conditional = new ConditionalComparator(matcher, bodyElementNumber, processor);
        }
        conditional.type = type;
        return conditional;
    }

    AbstractConditional(Matcher matcher, int bodyElementNumber, RunsProcessor processor) {
        conditionalStatement = matcher.group();
        conditionalExpressionRange = new DocumentRange(processor.getRunIdxAndPosition(bodyElementNumber, matcher.start()),
                processor.getRunIdxAndPosition(bodyElementNumber, matcher.end() - 1));
    }

    /**
     * Checks parents first.
     *
     * @param variables
     * @return
     */
    abstract boolean matches(Map<String, ?> variables);

    void setEndConditionalExpressionRange(DocumentRange endConditionalExpressionRange) {
        this.endConditionalExpressionRange = endConditionalExpressionRange;
    }

    @Transient
    public AbstractConditional getParent() {
        return parent;
    }

    public void setParent(AbstractConditional parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    void addChild(AbstractConditional child) {
        if (childConditionals == null) {
            childConditionals = new ArrayList<>();
        }
        childConditionals.add(child);
    }

    public List<AbstractConditional> getChildConditionals() {
        return childConditionals;
    }

    @Transient
    public DocumentRange getConditionalExpressionRange() {
        return conditionalExpressionRange;
    }

    @Transient
    public DocumentRange getEndConditionalExpressionRange() {
        return endConditionalExpressionRange;
    }

    @Transient
    public DocumentRange getRange() {
        if (range == null && endConditionalExpressionRange != null) {
            range = new DocumentRange(conditionalExpressionRange.getStartPosition(), endConditionalExpressionRange.getEndPosition());
        }
        return range;
    }

    public String getConditionalStatement() {
        return conditionalStatement;
    }

    public String getVariable() {
        return variable;
    }

    public ConditionalType getType() {
        return type;
    }

    @Override
    public int compareTo(AbstractConditional o) {
        return new CompareToBuilder()
                .append(conditionalExpressionRange.getStartPosition(), o.conditionalExpressionRange.getStartPosition()).toComparison();
    }

    @Override
    public String toString() {
        ToStringBuilder tos = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tos.append("conditionalStatement", conditionalStatement);
        tos.append("variable", variable);
        tos.append("type", type);
        tos.append("childConditionals", childConditionals);
        return tos.toString();
    }
}

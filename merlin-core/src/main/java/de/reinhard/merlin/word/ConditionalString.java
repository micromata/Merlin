package de.reinhard.merlin.word;

import de.reinhard.merlin.csv.CSVStringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;

public class ConditionalString extends AbstractConditional {

    private static Logger log = LoggerFactory.getLogger(ConditionalString.class);

    private String[] values;
    private boolean trimValues = true;

    ConditionalString(Matcher matcher, int bodyElementNumber, RunsProcessor processor) {
        super(matcher, bodyElementNumber, processor);
        variable = matcher.group(1);
        // String values
        values = CSVStringUtils.parseStringList(matcher.group(3), trimValues);
    }

    /**
     * Checks parents first.
     *
     * @param variables
     * @return
     */
    boolean matches(Map<String, ?> variables) {
        if (parent != null && parent.matches(variables) == false) {
            return false;
        }
        Object valueObject = variables.get(variable);
        if (valueObject == null) {
            return type.isIn(ConditionalType.NOT_EQUAL, ConditionalType.NOT_IN);
        }
        String value;
        if (trimValues) {
            value = valueObject.toString().trim();
        } else {
            value = valueObject.toString();
        }
        if (type.isIn(ConditionalType.EQUAL, ConditionalType.IN)) {
            for (String definedValue : values) {
                if (value.equals(definedValue)) {
                    return true;
                }
            }
            return false;
        }
        // tpye: not in:
        for (String definedValue : values) {
            if (value.equals(definedValue)) {
                return false;
            }
        }
        return true;
    }

    public String[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("variable", variable)
                .append("type", type)
                .append("Values", values)
                .append("conditionalExpressionRange", getConditionalExpressionRange())
                .append("endConditionalExpressionRange", getEndConditionalExpressionRange())
                .append("range", getRange())
                .toString();
    }
}

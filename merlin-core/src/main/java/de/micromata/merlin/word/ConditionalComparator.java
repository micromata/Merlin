package de.micromata.merlin.word;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;

public class ConditionalComparator extends AbstractConditional {

    private static Logger log = LoggerFactory.getLogger(ConditionalComparator.class);
    final static double EPSILON = 0.00001;

    private double doubleValue;
    private boolean not;

    ConditionalComparator(Matcher matcher, boolean not, int bodyElementNumber, RunsProcessor processor) {
        super(matcher, bodyElementNumber, processor);
        this.not = not;
        variable = matcher.group(2);
        // Number value
        String valString = matcher.group(4);
        try {
            if (valString.indexOf(',') >= 0) {
                valString = valString.replace(',', '.');
            }
            doubleValue = new Double(valString);
        } catch (NumberFormatException ex) {
            log.error("Can't parse integer value if statement: " + getConditionalStatement());
        }
    }

    /**
     * Checks parents first.
     *
     * @param variables
     * @return
     */
    boolean matches(Map<String, ?> variables) {
        boolean result = _matches(variables);
        return not ? !result : result;
    }

    boolean _matches(Map<String, ?> variables) {
        if (parent != null && parent.matches(variables) == false) {
            return false;
        }
        Object valueObject = variables.get(variable);
        if (valueObject == null) {
            return false;
        }
        double variableValue = 0;
        if (valueObject instanceof Number) {
            variableValue = ((Number) valueObject).doubleValue();
        } else {
            try {
                variableValue = new Double(valueObject.toString());
            } catch (NumberFormatException ex) {
                log.error("Can't parse variable ${" + variable + "} as integer: " + valueObject
                        + " in if-statement: " + getConditionalStatement());
            }
        }
        switch (type) {
            case LESS_EQUAL:
                return greaterThan(doubleValue, variableValue) || equalsEpsilon(variableValue, doubleValue);
            case LESS:
                return greaterThan(doubleValue, variableValue);
            case GREATER_EQUAL:
                return greaterThan(variableValue, doubleValue) || equalsEpsilon(variableValue, doubleValue);
            case GREATER:
                return greaterThan(variableValue, doubleValue);
        }
        return false;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("variable", variable)
                .append("type", type)
                .append("doubleValue", doubleValue)
                .append("conditionalExpressionRange", getConditionalExpressionRange())
                .append("endConditionalExpressionRange", getEndConditionalExpressionRange())
                .append("range", getRange())
                .toString();
    }

    public static boolean greaterThan(double a, double b) {
        return a - b > EPSILON;
    }

    public static boolean equalsEpsilon(double a, double b) {
        return a == b ? true : Math.abs(a - b) < EPSILON;
    }
}

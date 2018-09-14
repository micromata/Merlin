package de.reinhard.merlin.word.templating;

/**
 * For defining formats, such as number formats, date formats etc.
 */
public class TemplateWriterContext {

    public String toString(Object val, VariableType variableType) {
        if (val == null) {
            return "";
        }
        return val.toString();
    }
}

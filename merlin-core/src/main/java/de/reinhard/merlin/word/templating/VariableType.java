package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.word.ConditionalType;

public enum VariableType {
    STRING, INT, FLOAT, DATE;

    public boolean isIn(VariableType... types) {
        if (types == null) {
            return false;
        }
        for (VariableType type : types) {
            if (this == type) {
                return true;
            }
        }
        return false;
    }
}

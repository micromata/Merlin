package de.micromata.merlin.word.templating;

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

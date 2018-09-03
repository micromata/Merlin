package de.reinhard.merlin.word;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DocumentAction implements Comparable<DocumentAction> {
    private DocumentActionType type;
    private DocumentRange range;
    private String newText;

    public DocumentAction(DocumentActionType action, DocumentRange range) {
        this.type = action;
        this.range = range;
        if (type == DocumentActionType.REPLACE) {
            if (range.getStartPosition().getBodyElementNumber() != range.getEndPosition().getBodyElementNumber()) {
                throw new IllegalArgumentException("Action for replacing text supports only the replacement of text inside one paragraph (runs).");
            }
        }
    }

    public DocumentActionType getType() {
        return type;
    }

    public DocumentRange getRange() {
        return range;
    }

    public String getNewText() {
        if (type != DocumentActionType.REPLACE) {
            throw new IllegalArgumentException("getNewText() is only valid for type = REPLACE");
        }
        return newText;
    }

    public void setNewText(String newText) {
        if (type != DocumentActionType.REPLACE) {
            throw new IllegalArgumentException("getNewText() is only valid for type = REPLACE");
        }
        this.newText = newText;
    }

    @Override
    public int compareTo(DocumentAction o) {
        return range.getStartPosition().compareTo(o.range.getStartPosition());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(type).append(range).append(newText).toString();
    }
}

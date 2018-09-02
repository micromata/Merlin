package de.reinhard.merlin.word;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Localizes an object inside a word document.
 */
public class DocumentRange {
    private DocumentPosition startPosition;
    private DocumentPosition endPosition;


    DocumentRange(DocumentPosition startPosition, DocumentPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    void setEndPosition(DocumentPosition endPosition) {
        this.endPosition = endPosition;
    }

    @Override
    public String toString() {
        return "[range=[start=" + startPosition + ", end=" + endPosition + "]";
    }

    public DocumentPosition getStartPosition() {
        return startPosition;
    }

    public DocumentPosition getEndPosition() {
        return endPosition;
    }
}

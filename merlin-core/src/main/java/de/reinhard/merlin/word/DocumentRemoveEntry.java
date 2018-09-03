package de.reinhard.merlin.word;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DocumentRemoveEntry implements Comparable<DocumentRemoveEntry> {
    private DocumentRange range;

    public DocumentRemoveEntry(DocumentRange range) {
        this.range = range;
    }

    public DocumentRange getRange() {
        return range;
    }

    @Override
    public int compareTo(DocumentRemoveEntry o) {
        return range.getStartPosition().compareTo(o.range.getStartPosition());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(range).toString();
    }
}

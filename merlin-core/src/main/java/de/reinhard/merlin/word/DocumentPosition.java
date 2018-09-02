package de.reinhard.merlin.word;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Localizes an object inside a word document.
 */
public class DocumentPosition implements Comparable<DocumentPosition> {
    private int bodyElementNumber;
    private int runIndex;
    private int runCharAt;


    DocumentPosition(int bodyElementNumber, int runIndex, int runPos) {
        this.bodyElementNumber = bodyElementNumber;
        this.runIndex = runIndex;
        this.runCharAt = runPos;
    }

    @Override
    public int compareTo(DocumentPosition o) {
        if (o == null) {
            // this is greater than other (null).
            return 1;
        }
        return new CompareToBuilder()
                .append(this.bodyElementNumber, o.bodyElementNumber)
                .append(this.runIndex, o.runIndex)
                .append(this.runCharAt, o.runCharAt)
                .toComparison();
    }

    @Override
    public String toString() {
        return "[body-element=" + bodyElementNumber + ", runs-idx=" + runIndex + ", charAt=" + runCharAt + "]";
    }

    public int getBodyElementNumber() {
        return bodyElementNumber;
    }

    public int getRunIndex() {
        return runIndex;
    }

    public int getRunCharAt() {
        return runCharAt;
    }
}

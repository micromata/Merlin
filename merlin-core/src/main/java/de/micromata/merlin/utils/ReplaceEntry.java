package de.micromata.merlin.utils;

public class ReplaceEntry implements Comparable<ReplaceEntry> {
    public int start;
    public int end;
    public String newText;

    public ReplaceEntry(int start, int end, String newText) {
        this.start = start;
        this.end = end;
        this.newText = newText;
    }

    @Override
    public int compareTo(ReplaceEntry o) {
        if (start == o.start) {
            return 0;
        } else if (start < o.start) {
            return -1;
        }
        return 1;
    }
}
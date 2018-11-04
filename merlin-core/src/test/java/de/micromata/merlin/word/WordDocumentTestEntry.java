package de.micromata.merlin.word;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

class WordDocumentTestEntry {
    XWPFParagraph par;
    String[] expected;

    WordDocumentTestEntry(XWPFParagraph par, String... expected) {
        this.par = par;
        this.expected = expected;
    }
}

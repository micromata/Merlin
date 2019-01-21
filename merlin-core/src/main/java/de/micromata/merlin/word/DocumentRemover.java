package de.micromata.merlin.word;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DocumentRemover {
    private Logger log = LoggerFactory.getLogger(DocumentRemover.class);

    private List<DocumentRange> modifiers;
    private WordDocument document;
    private Map<Integer, IBodyElement> bodyElementsMap;

    public DocumentRemover(WordDocument document) {
        this.document = document;
        init();
    }

    /**
     * Removes all registered ranges.
     * @return this for chaining.
     */
    public DocumentRemover action() {
        init();
        Collections.sort(modifiers, Collections.reverseOrder());
        for (DocumentRange range : modifiers) {
            removeRange(range);
        }
        return this;
    }

    private void init() {
        if (bodyElementsMap != null) {
            // Already initialized.
            return;
        }
        int bodyElementCounter = 0;
        bodyElementsMap = new HashMap<>();
        for (IBodyElement element : document.getDocument().getBodyElements()) {
            bodyElementsMap.put(bodyElementCounter++, element);
        }
        modifiers = new ArrayList<>();
    }

    /**
     * First register all ranges to remove. The ranges should be removed in reverse order (beginning from the end
     * of the document to preserve the body element index for next ranges to remove.
     *
     * @param rangeToRemove The range to remove.
     */
    public void add(DocumentRange rangeToRemove) {
        init();
        for (DocumentRange mod : modifiers) {
            if (mod.isIn(rangeToRemove.getStartPosition()) ||
                    mod.isIn(rangeToRemove.getEndPosition())) {
                log.warn("Given rangeToRemove collidates with already existing range. Existing: " + mod + ", new: " + rangeToRemove);
            }
        }
        modifiers.add(rangeToRemove);
    }


    /**
     * Removes ranges in reverse order (starting from the end of the document). Paragraphs to remove will only be marked as to remove.
     *
     * @param removeRange
     */
    private void removeRange(DocumentRange removeRange) {
        int fromNo = removeRange.getStartPosition().getBodyElementNumber();
        int toNo = removeRange.getEndPosition().getBodyElementNumber();
        XWPFDocument doc = document.getDocument();
        for (int elementNo = toNo; elementNo >= fromNo; elementNo--) { // Reverse order to prevent removing of paragraphs and changing the body element index.
            IBodyElement element = bodyElementsMap.get(elementNo);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                if (elementNo == fromNo) {
                    RunsProcessor processor = new RunsProcessor(((XWPFParagraph) element));
                    if (elementNo == toNo) {
                        processor.replaceText(removeRange.getStartPosition(), removeRange.getEndPosition(), "");
                        if (processor.getText().length() == 0) {
                            doc.removeBodyElement(elementNo);
                        }
                    } else {
                        processor.replaceText(removeRange.getStartPosition(), processor.getEnd(elementNo), "");
                        if (processor.getText().length() == 0) {
                            doc.removeBodyElement(elementNo);
                        }
                    }
                } else if (elementNo == toNo) {
                    RunsProcessor processor = new RunsProcessor(((XWPFParagraph) element));
                    processor.replaceText(new DocumentPosition(elementNo, 0, 0), removeRange.getEndPosition(), "");
                    if (processor.getText().length() == 0) {
                        doc.removeBodyElement(elementNo);
                    }
                } else {
                    doc.removeBodyElement(elementNo);
                }
            } else {
                doc.removeBodyElement(elementNo);
            }
        }
    }
}

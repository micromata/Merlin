package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DocumentRemover {
    private Logger log = LoggerFactory.getLogger(DocumentRemover.class);

    private List<DocumentRemoveEntry> modifiers = new ArrayList<>();
    private WordDocument document;
    private Map<Integer, IBodyElement> bodyElementsMap;
    private Set<Integer> paragraphsToRemove = new HashSet<>();

    public DocumentRemover(WordDocument document) {
        this.document = document;
        int bodyElementCounter = 0;
        bodyElementsMap = new HashMap<>();
        for (IBodyElement element : document.getDocument().getBodyElements()) {
            bodyElementsMap.put(bodyElementCounter++, element);
        }
    }

    /**
     * @return this for chaining.
     */
    public DocumentRemover action() {
        Collections.sort(modifiers, Collections.reverseOrder());
        for (DocumentRemoveEntry action : modifiers) {
            removeRange(action.getRange());
        }
        return this;
    }

    public void add(DocumentRemoveEntry modifier) {
        for (DocumentRemoveEntry mod : modifiers) {
            if (mod.getRange().isIn(modifier.getRange().getStartPosition()) ||
                    mod.getRange().isIn(modifier.getRange().getEndPosition())) {
                log.warn("Given modifier collidates with already existing range. Existing: " + mod + ", new: " + modifier);
            }
        }
        modifiers.add(modifier);
    }

    void removeMarkedParagraphs() {
        XWPFDocument doc = document.getDocument();
        List<IBodyElement> elements = doc.getBodyElements();
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (!(elements.get(i) instanceof XWPFParagraph)) {
                continue;
            }
            XWPFParagraph par = (XWPFParagraph) elements.get(i);
            if (paragraphsToRemove.contains(i)) {
                doc.removeBodyElement(i);
            }
        }
    }

    private void removeRange(DocumentRange removeRange) {
        int fromNo = removeRange.getStartPosition().getBodyElementNumber();
        int toNo = removeRange.getEndPosition().getBodyElementNumber();
        for (int elementNo = fromNo; elementNo <= toNo; elementNo++) {
            IBodyElement element = bodyElementsMap.get(elementNo);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                if (elementNo == fromNo) {
                    RunsProcessor processor = new RunsProcessor(((XWPFParagraph) element).getRuns());
                    if (elementNo == toNo) {
                        processor.replaceText(removeRange.getStartPosition(), removeRange.getEndPosition(), "");
                        if (processor.getText().length() == 0) {
                            paragraphsToRemove.add(elementNo);
                        }
                    } else {
                        processor.replaceText(removeRange.getStartPosition(), processor.getEnd(elementNo), "");
                        if (processor.getText().length() == 0) {
                            paragraphsToRemove.add(elementNo);
                        }
                    }
                } else if (elementNo == toNo) {
                    RunsProcessor processor = new RunsProcessor(((XWPFParagraph) element).getRuns());
                    processor.replaceText(new DocumentPosition(elementNo, 0, 0), removeRange.getEndPosition(), "");
                    if (processor.getText().length() == 0) {
                        paragraphsToRemove.add(elementNo);
                    }
                } else {
                    paragraphsToRemove.add(elementNo);
                }
            } else {
                // Not yet supported.
            }
        }
    }
}

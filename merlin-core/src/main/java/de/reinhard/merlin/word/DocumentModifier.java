package de.reinhard.merlin.word;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DocumentModifier {
    private Logger log = LoggerFactory.getLogger(DocumentModifier.class);

    private TreeSet<DocumentAction> modifiers = new TreeSet<>();
    private WordDocument document;
    private Map<Integer, IBodyElement> bodyElementsMap;
    private Set<Integer> paragraphsToRemove = new HashSet<>();

    public DocumentModifier(WordDocument document) {
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
    public DocumentModifier action() {
        Iterator<DocumentAction> it = modifiers.descendingIterator();
        while (it.hasNext()) {
            DocumentAction action = it.next();
            switch (action.getType()) {
                case REMOVE:
                    removeRange(action.getRange());
                    break;
                case REPLACE:
                    break;
                case INSERT:
                    throw new UnsupportedOperationException("Action type " + action.getType() + " not yet implementd.");
            }
        }
        return this;
    }

    public void add(DocumentAction modifier) {
        for (DocumentAction mod : modifiers) {
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

    void markParagraphToRemove(XWPFParagraph paragraph) {
        XWPFDocument doc = document.getDocument();
        List<IBodyElement> elements = doc.getBodyElements();
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i) == paragraph) {
                paragraphsToRemove.add(i);
                return;
            }
        }
        log.error("Paragraph not found to remove: " + paragraph.getText());
    }

    private void replace(DocumentAction action) {
        DocumentRange range = action.getRange();
        if (range.getStartPosition().getBodyElementNumber() != range.getEndPosition().getBodyElementNumber()) {
            throw new IllegalArgumentException("Can only replace text inside one paragraph (runs).");
        }
        IBodyElement element = bodyElementsMap.get(range.getStartPosition().getBodyElementNumber());
        if (!(element instanceof XWPFParagraph)) {
            throw new IllegalArgumentException("Replacement is only supported for paragraphs, not for " + element.getClass().toString());
        }
        RunsProcessor processor = new RunsProcessor(((XWPFParagraph) element).getRuns());
        processor.replaceText(range.getStartPosition(), range.getEndPosition(), action.getNewText());
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

package de.reinhard.merlin.excel;

import de.reinhard.merlin.I18n;
import org.apache.poi.ss.usermodel.*;

public class ExcelWriterContext {
    private boolean addCellComments = true;
    private boolean addErrorColumn;
    private boolean highlightErrorCells = true;
    private boolean addErrorSheet;
    private CellStyle errorHighlightCellStyle;
    private CellStyle errorColumnCellStyle;
    private ExcelWorkbook workbook;
    private I18n i18n;
    private int maxErrorMessagesPerColumnPercent = 10;
    private int maxErrorMessagesPerColumn = 100;
    private ExcelValidationErrorCellHighlighter cellHighlighter;
    private ExcelValidationErrorCommentWriter commentWriter;
    private ExcelValidationErrorMessageWriter errorMessageWriter;


    public ExcelWriterContext(I18n i18n, ExcelWorkbook workbook) {
        this.i18n = i18n;
        this.workbook = workbook;
    }

    /**
     * Default is false.
     *
     * @return true for adding a new sheet with all collected validation error messages.
     */
    public boolean isAddErrorSheet() {
        return addErrorSheet;
    }

    /**
     * @param addErrorSheet
     * @return this for chaining.
     */
    public ExcelWriterContext setAddErrorSheet(boolean addErrorSheet) {
        this.addErrorSheet = addErrorSheet;
        return this;
    }

    public boolean isAddCellComments() {
        return addCellComments;
    }

    /**
     * @param addCellComments
     * @return this for chaining.
     */
    public ExcelWriterContext setAddCellComments(boolean addCellComments) {
        this.addCellComments = addCellComments;
        return this;
    }

    public boolean isAddErrorColumn() {
        return addErrorColumn;
    }

    /**
     * @param addErrorColumn
     * @return this for chaining.
     */
    public ExcelWriterContext setAddErrorColumn(boolean addErrorColumn) {
        this.addErrorColumn = addErrorColumn;
        return this;
    }

    public boolean isHighlightErrorCells() {
        return highlightErrorCells;
    }

    public void setHighlightErrorCells(boolean highlightErrorCells) {
        this.highlightErrorCells = highlightErrorCells;
    }

    public CellStyle getErrorHighlightCellStyle() {
        if (errorHighlightCellStyle == null) {
            errorHighlightCellStyle = workbook.createOrGetCellStyle("error-highlight-cell-style");
            errorHighlightCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            errorHighlightCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return errorHighlightCellStyle;
    }

    public void setErrorHighlightCellStyle(CellStyle errorHighlightCellStyle) {
        this.errorHighlightCellStyle = errorHighlightCellStyle;
    }

    public CellStyle getErrorColumnCellStyle() {
        if (errorColumnCellStyle == null) {
            errorColumnCellStyle = workbook.createOrGetCellStyle("error-column-cell-style");
            final Font font = workbook.createOrGetFont("error-column-cell-font");
            font.setFontName("Arial");
            font.setColor(IndexedColors.RED.index);
            errorColumnCellStyle.setFont(font);
            errorColumnCellStyle.setWrapText(true);
        }
        return errorColumnCellStyle;
    }

    public void setErrorColumnCellStyle(CellStyle errorColumnCellStyle) {
        this.errorColumnCellStyle = errorColumnCellStyle;
    }

    public ExcelValidationErrorCellHighlighter getCellHighlighter() {
        if (cellHighlighter == null) {
            cellHighlighter = new ExcelValidationErrorCellHighlighter();
        }
        return cellHighlighter;
    }

    /**
     * For customizing cell highlighting. For styling you can also use {@link #setErrorHighlightCellStyle(CellStyle)}.
     *
     * @param cellHighlighter
     */
    public void setCellHighlighter(ExcelValidationErrorCellHighlighter cellHighlighter) {
        this.cellHighlighter = cellHighlighter;
    }

    public ExcelValidationErrorCommentWriter getCommentWriter() {
        if (commentWriter == null) {
            commentWriter = new ExcelValidationErrorCommentWriter();
        }
        return commentWriter;
    }

    /**
     * For customizing comments in error cells.
     *
     * @param commentWriter
     */
    public void setCommentWriter(ExcelValidationErrorCommentWriter commentWriter) {
        this.commentWriter = commentWriter;
    }

    public ExcelValidationErrorMessageWriter getErrorMessageWriter() {
        if (errorMessageWriter == null) {
            errorMessageWriter = new ExcelValidationErrorMessageWriter();
        }
        return errorMessageWriter;
    }

    /**
     * For customizing error messages in error message column.
     *
     * @param errorMessageWriter
     */
    public void setErrorMessageWriter(ExcelValidationErrorMessageWriter errorMessageWriter) {
        this.errorMessageWriter = errorMessageWriter;
    }

    public I18n getI18n() {
        return i18n;
    }
}

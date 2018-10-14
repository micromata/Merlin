package de.reinhard.merlin.excel;

import de.reinhard.merlin.I18n;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Context while writing Excel files for customizing CellStyle of error cells, localization (I18N) etc.
 */
public class ExcelWriterContext {
    private boolean addCellComments = true;
    private boolean addErrorColumn;
    private boolean highlightErrorCells = true;
    private boolean highlightColumnHeadCells = true;
    private boolean addErrorSheet;
    private CellStyle errorHighlightCellStyle;
    private CellStyle errorColumnCellStyle;
    private CellStyle cleanCellStyle;
    private ExcelWorkbook workbook;
    private I18n i18n;
    private int maxErrorMessagesPerColumnPercent = 10;
    private int maxErrorMessagesPerColumn = 100;
    private ExcelValidationErrorCellHighlighter cellHighlighter;
    private ExcelValidationErrorCellCleaner cellCleaner;
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

    public boolean isHighlightColumnHeadCells() {
        return highlightColumnHeadCells;
    }

    public void setHighlightColumnHeadCells(boolean highlightColumnHeadCells) {
        this.highlightColumnHeadCells = highlightColumnHeadCells;
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

    public ExcelValidationErrorCellCleaner getCellCleaner() {
        if (cellCleaner == null) {
            cellCleaner = new ExcelValidationErrorCellCleaner();
        }
        return cellCleaner;
    }

    public void setCellCleaner(ExcelValidationErrorCellCleaner cellCleaner) {
        this.cellCleaner = cellCleaner;
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
        return this.i18n;
    }
}

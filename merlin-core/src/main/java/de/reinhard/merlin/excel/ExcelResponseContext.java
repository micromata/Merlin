package de.reinhard.merlin.excel;

import org.apache.poi.ss.usermodel.*;

public class ExcelResponseContext {
    private boolean addCellComments = true;
    private boolean addErrorColumn;
    private boolean highlightErrorCells = true;
    private CellStyle errorHighlightCellStyle;
    private CellStyle errorColumnCellStyle;
    private Workbook workbook;

    public ExcelResponseContext(ExcelWorkbook workbook) {
        this.workbook = workbook.getPOIWorkbook();
    }

    public ExcelResponseContext(Workbook workbook) {
        this.workbook = workbook;
    }

    public boolean isAddCellComments() {
        return addCellComments;
    }

    /**
     * @param addCellComments
     * @return this for chaining.
     */
    public ExcelResponseContext setAddCellComments(boolean addCellComments) {
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
    public ExcelResponseContext setAddErrorColumn(boolean addErrorColumn) {
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
            errorHighlightCellStyle = workbook.createCellStyle();
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
            errorColumnCellStyle = workbook.createCellStyle();
            final Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setColor(IndexedColors.RED.index);
            errorColumnCellStyle.setFont(font);
        }
        return errorColumnCellStyle;
    }

    public void setErrorColumnCellStyle(CellStyle errorColumnCellStyle) {
        this.errorColumnCellStyle = errorColumnCellStyle;
    }
}

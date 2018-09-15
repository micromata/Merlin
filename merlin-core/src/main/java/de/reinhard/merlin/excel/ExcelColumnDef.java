package de.reinhard.merlin.excel;

import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExcelColumnDef {
    private Logger log = LoggerFactory.getLogger(ExcelColumnDef.class);

    private int columnNumber = -1;
    private String columnHeadname;
    private List<ExcelColumnListener> columnListeners;

    ExcelColumnDef(String columnHeadname) {
        this.columnHeadname = columnHeadname != null ? columnHeadname : CellReference.convertNumToColString(columnNumber);
    }

    ExcelColumnDef(int columnNumber, String columnHeadname) {
        this.columnNumber = columnNumber;
        this.columnHeadname = columnHeadname != null ? columnHeadname : CellReference.convertNumToColString(columnNumber);
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * @return Column head name (1st row) if given, otherwise {@link #getColumnNumberAsLetters()}.
     */
    public String getColumnHeadname() {
        return columnHeadname;
    }

    /**
     * @return Column number as letters: A, B, ..., AA, AB, ...
     */
    public String getColumnNumberAsLetters() {
        return CellReference.convertNumToColString(columnNumber);
    }

    public boolean hasColumnListeners() {
        return columnListeners != null && columnListeners.size() > 0;
    }
    public List<ExcelColumnListener> getColumnListeners() {
        return columnListeners;
    }

    public void addColumnListener(ExcelColumnListener columnListener) {
        if (this.columnListeners == null) {
            columnListeners = new ArrayList<>();
        }
        columnListeners.add(columnListener);
        columnListener.setColumnDef(this);
    }
}

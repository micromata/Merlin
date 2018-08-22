package de.reinhard.merlin.excel;

import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExcelColumnDef {
    private Logger log = LoggerFactory.getLogger(ExcelColumnDef.class);

    private int columnNumber;
    private String columnHeadname;
    private String columnNumberAsLetters;
    private List<ColumnListener> columnListeners;

    ExcelColumnDef(int columnNumber, String columnHeadname) {
        this.columnNumber = columnNumber;
        this.columnNumberAsLetters = CellReference.convertNumToColString(columnNumber);
        this.columnHeadname = columnHeadname != null ? columnHeadname : columnNumberAsLetters;
    }

    public int getColumnNumber() {
        return columnNumber;
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
        return columnNumberAsLetters;
    }

    public boolean hasColumnListeners() {
        return columnListeners != null && columnListeners.size() > 0;
    }
    public List<ColumnListener> getColumnListeners() {
        return columnListeners;
    }

    public void addColumnListener(ColumnListener columnListener) {
        if (this.columnListeners == null) {
            columnListeners = new LinkedList<>();
        }
        columnListeners.add(columnListener);
        columnListener.setColumnDef(this);
    }
}

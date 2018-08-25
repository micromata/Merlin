package de.reinhard.merlin.excel;

import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class ExcelColumnDef {
    private Logger log = LoggerFactory.getLogger(ExcelColumnDef.class);

    private int columnNumber;
    private String columnHeadname;
    private List<ColumnListener> columnListeners;

    ExcelColumnDef(int columnNumber, String columnHeadname) {
        this.columnNumber = columnNumber;
        this.columnHeadname = columnHeadname != null ? columnHeadname : CellReference.convertNumToColString(columnNumber);
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
        return CellReference.convertNumToColString(columnNumber);
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

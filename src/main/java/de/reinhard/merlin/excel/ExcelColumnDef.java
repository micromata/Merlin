package de.reinhard.merlin.excel;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellReference;

import java.util.HashMap;
import java.util.Map;

public class ExcelColumnDef {
    private static final Logger log = Logger.getLogger(ExcelColumnDef.class);

    private int columnNumber;
    private String columnHeadname;
    private String columnNumberAsLetters;
    private ColumnValidator columnValidator;
    private Map<String, ColumnListener> columnListenerMap = new HashMap<>();

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

    public ColumnValidator getColumnValidator() {
        return columnValidator;
    }

    public void setColumnValidator(ColumnValidator columnValidator) {
        if (this.columnValidator != null) {
            log.error("Oups, trying to add column validator to " + columnNumber + ". column '" + getColumnHeadname() + "' twice. Ignoring duplicate validator.");
            return;
        }
        this.columnValidator = columnValidator;
        this.columnValidator.setColumnDef(this);
    }

    /**
     *
     * @param name
     * @param listener
     * @return this for chaining.
     */
    public ExcelColumnDef register(String name, ColumnListener listener) {
        columnListenerMap.put(name, listener);
        return this;
    }

    public ColumnListener getColumnListener(String name) {
        return columnListenerMap.get(name);
    }
}

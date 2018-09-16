package de.reinhard.merlin.word.templating;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.excel.ExcelCell;
import de.reinhard.merlin.excel.ExcelWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.DateFormatConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * For defining formats, such as number formats, date formats etc.
 */
public class TemplateContext {
    private Logger log = LoggerFactory.getLogger(VariableDefinition.class);
    private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

    DateFormat dateFormatter;
    DateFormat[] dateFormatters;
    Locale locale;
    private String excelDateFormatPattern;
    private I18n i18n;

    public TemplateContext() {
        this(Locale.getDefault());
    }

    public TemplateContext(Locale locale) {
        dateFormatter = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        dateFormatters = new DateFormat[]{
                new SimpleDateFormat("yyyy-MM-dd"),
                new SimpleDateFormat("d/M/yyyy"),
                new SimpleDateFormat("d/M/yy"),
                new SimpleDateFormat("dd.MM.yyyy"),
                new SimpleDateFormat("dd.MM.yy"),
                new SimpleDateFormat("d.M.yyyy"),
                new SimpleDateFormat("d.M.yy")
        };
        excelDateFormatPattern = DateFormatConverter.convert(locale, DEFAULT_DATE_PATTERN);
    }

    /**
     * Default is "dd/MM/yyyy".
     *
     * @param pattern
     */
    public void setDateFormat(String pattern) {
        setDateFormat(pattern, locale);
    }

    public DateFormat getDateFormatter() {
        return dateFormatter;
    }

    /**
     * Default is "dd/MM/yyyy".
     *
     * @param pattern
     */
    public void setDateFormat(String pattern, Locale locale) {
        this.locale = locale;
        dateFormatter = new SimpleDateFormat(pattern, locale);
        excelDateFormatPattern = DateFormatConverter.convert(locale, pattern);
    }

    public String toString(Object val, VariableType variableType) {
        if (val == null) {
            return "";
        }
        return val.toString();
    }

    public Object convertValue(Object value, VariableType type) {
        if (value == null) {
            return null;
        }
        switch (type) {
            case INT:
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                if (value instanceof String) {
                    try {
                        return new Integer((String) value);
                    } catch (NumberFormatException ex) {
                        log.error("Can't parse integer '" + value + "': " + ex.getMessage(), ex);
                        return 0;
                    }
                }
                log.error("Can't get integer from type " + value.getClass().getCanonicalName() + ": " + value);
                return 0;
            case FLOAT:
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                if (value instanceof String) {
                    try {
                        return new Double((String) value);
                    } catch (NumberFormatException ex) {
                        log.error("Can't parse float '" + value + "': " + ex.getMessage(), ex);
                        return 0.0;
                    }
                }
                log.error("Can't get float from type " + value.getClass().getCanonicalName() + ": " + value);
                return 0.0;
            case STRING:
                return value.toString();
            case DATE:
                if (value instanceof Date) {
                    return value;
                } else if (value instanceof String) {
                    if (((String) value).trim().length() == 0) {
                        return null;
                    }
                    return parseDate(value);
                }
                log.error("Can't get date from type " + value.getClass().getCanonicalName() + ": " + value);
        }
        return value;
    }

    public Date parseDate(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Date) {
            return (Date) val;
        }
        if (val instanceof String) {
            Date date = parseDate(dateFormatter, (String) val);
            if (date != null) {
                return date;
            }
            for (DateFormat formatter : dateFormatters) {
                date = parseDate(formatter, (String) val);
                if (date != null) {
                    return date;
                }
            }
        }
        log.error("Can't parse date: " + val + " from type " + val.getClass());
        return null;
    }

    private Date parseDate(DateFormat dateFormatter, String val) {
        try {
            Date parsedDate = dateFormatter.parse(val);
            if (parsedDate == null) {
                return null;
            }
            return parsedDate;
        } catch (ParseException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Can't parse date '" + val + "': " + ex.getMessage());
            }
        }
        return null;
    }

    public String getBooleanAsString(boolean value) {
        return value ? "X" : "";
    }

    public void setCellValue(ExcelWorkbook workbook, Cell cell, Object valueObject, VariableType type) {
        Object value = convertValue(valueObject, type);
        if (value == null) {
            cell.setCellType(CellType.BLANK);
            return;
        }
        if (type == VariableType.FLOAT) {
            ExcelCell.setCellValue(workbook, cell, (double) value);
        } else if (type == VariableType.INT) {
            ExcelCell.setCellValue(workbook, cell, (int) value);
        } else if (type == VariableType.DATE) {
            ExcelCell.setCellValue(workbook, cell, excelDateFormatPattern, (Date) value);
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    public I18n getI18n() {
        if (i18n == null) {
            return I18n.getDefault();
        }
        return i18n;
    }
}

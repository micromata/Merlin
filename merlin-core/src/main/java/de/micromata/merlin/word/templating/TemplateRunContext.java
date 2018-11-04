package de.micromata.merlin.word.templating;

import de.micromata.merlin.CoreI18n;
import de.micromata.merlin.I18n;
import de.micromata.merlin.excel.ExcelCell;
import de.micromata.merlin.excel.ExcelWorkbook;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.DateFormatConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * For defining formats, such as number formats, date formats etc.
 */
public class TemplateRunContext {
    private Logger log = LoggerFactory.getLogger(VariableDefinition.class);
    private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

    DateFormat dateFormatter;
    DateFormat[] dateFormatters;
    DateFormat dateFormatterGerman;

    Locale locale;
    private String excelDateFormatPattern;
    private I18n i18n;

    public TemplateRunContext() {
        i18n = CoreI18n.getDefault();
        this.locale = Locale.getDefault();
        dateFormatter = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        dateFormatterGerman = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatters = new DateFormat[]{
                new SimpleDateFormat("yyyy-MM-dd"),
                new SimpleDateFormat("d/M/yyyy"),
                new SimpleDateFormat("d/M/yy"),
                dateFormatterGerman,
                new SimpleDateFormat("dd.MM.yy"),
                new SimpleDateFormat("d.M.yyyy"),
                new SimpleDateFormat("d.M.yy")
        };
        excelDateFormatPattern = DateFormatConverter.convert(locale, DEFAULT_DATE_PATTERN);
    }

    /**
     * Convert the values matching the user's locale.
     *
     * @param variables
     * @param templateDefinition For getting definitions of variables. If not given, the parameter variables is returen unchanged.
     * @return
     */
    public Map<String, Object> convertVariables(Map<String, Object> variables, TemplateDefinition templateDefinition) {
        if (templateDefinition == null) {
            return variables;
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String varname = entry.getKey();
            VariableDefinition variableDefinition = templateDefinition.getVariableDefinition(varname);
            Object value = entry.getValue();
            if (variableDefinition != null) {
                if (variableDefinition.getType() == VariableType.DATE) {
                    Date date = parseDate(value);
                    if (date != null) {
                        value = dateToString(date);
                    }
                }
            }
            result.put(entry.getKey(), value);
        }
        return result;
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
                    if (StringUtils.isBlank((String) value)) {
                        return null;
                    }
                    try {
                        return new Integer((String) value);
                    } catch (NumberFormatException ex) {
                        log.warn("Can't parse integer '" + value + "': " + ex.getMessage());
                        return null;
                    }
                }
                log.warn("Can't get integer from type " + value.getClass().getCanonicalName() + ": " + value);
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
                        return null;
                    }
                }
                log.error("Can't get float from type " + value.getClass().getCanonicalName() + ": " + value);
                return null;
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

    public String dateToString(Date date) {
        if (locale == null) {
            return dateFormatter.format(date);
        } else if (locale.getLanguage().equals("de")) {
            return dateFormatterGerman.format(date);
        }
        return dateFormatter.format(date);
    }

    public static String getBooleanAsString(boolean value) {
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

    public void setI18n(I18n i18N) {
        this.i18n = i18n;
    }

    public I18n getI18n() {
        return i18n;
    }

    /**
     * Sets i18n and locale. If you want to use your own i18n, please call {@link #setI18n(I18n)} after {@link #setLocale(Locale)}.
     *
     * @param locale
     */
    public void setLocale(Locale locale) {
        i18n = CoreI18n.getDefault().get(locale);
        this.locale = locale;
    }
}

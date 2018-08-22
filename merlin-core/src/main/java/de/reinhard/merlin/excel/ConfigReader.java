package de.reinhard.merlin.excel;

import de.reinhard.merlin.data.PropertiesStorage;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ConfigReader {
    private static final Logger log = Logger.getLogger(ConfigReader.class);

    private ExcelSheet sheet;
    private ExcelColumnDef propertyColumnDef;
    private ExcelColumnDef valueColumnDef;

    public ConfigReader(ExcelSheet sheet, String propertyColumnHeadname, String valueColumnHeadname) {
        this(sheet, sheet.getColumnDef(propertyColumnHeadname), sheet.getColumnDef(valueColumnHeadname));
    }

    public ConfigReader(ExcelSheet sheet, int propertyColumnNumber, int valueColumnNumber) {
        this(sheet, sheet.getColumnDef(propertyColumnNumber), sheet.getColumnDef(valueColumnNumber));
    }

    private ConfigReader(ExcelSheet sheet, ExcelColumnDef propertyColumnDef, ExcelColumnDef valueColumnDef) {
        this.sheet = sheet;
        this.propertyColumnDef = propertyColumnDef;
        this.valueColumnDef = valueColumnDef;
        sheet.add(this.propertyColumnDef, new ColumnValidator().setUnique());
        sheet.add(this.propertyColumnDef, new ColumnValidator());
        sheet.analyze(true);
    }

    public PropertiesStorage readConfig(ExcelWorkbook excelReader) {
        PropertiesStorage properties = new PropertiesStorage();
        int counter = 0;
        while (sheet.hasNextRow()) {
            sheet.nextRow();
            String property = sheet.getCell("Property");
            String value = sheet.getCell("Value");
            if (StringUtils.isNotEmpty(value)) {
                log.info("Read config property '" + property + "'='" + value + "'");
                properties.setConfig(property, value);
            }
        }
        return properties;
    }
}

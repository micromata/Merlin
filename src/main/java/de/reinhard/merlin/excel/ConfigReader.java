package de.reinhard.merlin.excel;

import de.reinhard.merlin.data.PropertiesStorage;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ConfigReader {
    private static final Logger log = Logger.getLogger(ConfigReader.class);

    private static final String SHEET_NAME = "Config";

    public PropertiesStorage readConfig(ExcelWorkbook excelReader) {
        ExcelSheet sheet = excelReader.getSheet(SHEET_NAME);
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

package de.reinhard.merlin.excel;

import de.reinhard.merlin.I18n;
import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.data.PropertiesStorage;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ConfigReader {
    private Logger log = LoggerFactory.getLogger(ConfigReader.class);

    private ExcelSheet sheet;
    private ExcelColumnDef propertyColumnDef;
    private ExcelColumnDef valueColumnDef;
    private PropertiesStorage propertiesStorage;

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
        propertiesStorage = new PropertiesStorage();
        int counter = 0;
        while (sheet.hasNextRow()) {
            sheet.nextRow();
            String property = PoiHelper.getValueAsString(sheet.getCell(propertyColumnDef));
            String value = PoiHelper.getValueAsString(sheet.getCell(valueColumnDef));
            if (StringUtils.isNotEmpty(value)) {
                log.info("Read config property '" + property + "'='" + value + "'");
                propertiesStorage.setConfig(property, value);
            }
        }
        return propertiesStorage;
    }

    public ExcelSheet getSheet() {
        return sheet;
    }
}

package de.reinhard.merlin.excel;

import de.reinhard.merlin.data.PropertiesStorage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelConfigReader {
    private Logger log = LoggerFactory.getLogger(ExcelConfigReader.class);

    private ExcelSheet sheet;
    private ExcelColumnDef propertyColumnDef;
    private ExcelColumnDef valueColumnDef;
    private PropertiesStorage propertiesStorage;

    public ExcelConfigReader(ExcelSheet sheet, String propertyColumnHeadname, String valueColumnHeadname) {
        this(sheet, sheet.getColumnDef(propertyColumnHeadname), sheet.getColumnDef(valueColumnHeadname));
    }

    public ExcelConfigReader(ExcelSheet sheet, int propertyColumnNumber, int valueColumnNumber) {
        this(sheet, sheet.getColumnDef(propertyColumnNumber), sheet.getColumnDef(valueColumnNumber));
    }

    private ExcelConfigReader(ExcelSheet sheet, ExcelColumnDef propertyColumnDef, ExcelColumnDef valueColumnDef) {
        this.sheet = sheet;
        this.propertyColumnDef = propertyColumnDef;
        this.valueColumnDef = valueColumnDef;
        sheet.add(this.propertyColumnDef, new ExcelColumnValidator().setUnique());
        sheet.add(this.propertyColumnDef, new ExcelColumnValidator());
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

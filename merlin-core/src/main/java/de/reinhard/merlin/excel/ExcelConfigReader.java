package de.reinhard.merlin.excel;

import de.reinhard.merlin.data.PropertiesStorage;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class ExcelConfigReader {
    private Logger log = LoggerFactory.getLogger(ExcelConfigReader.class);

    private ExcelSheet sheet;
    private ExcelColumnDef propertyColumnDef;
    private ExcelColumnDef valueColumnDef;
    private PropertiesStorage propertiesStorage;

    public ExcelConfigReader(ExcelSheet sheet, String propertyColumnHeadname, String valueColumnHeadname) {
        this.sheet = sheet;
        sheet.registerColumns(valueColumnHeadname);
        sheet.registerColumn(propertyColumnHeadname, new ExcelColumnValidator().setUnique());
        this.propertyColumnDef = sheet.getColumnDef(propertyColumnHeadname);
        this.valueColumnDef = sheet.getColumnDef(valueColumnHeadname);
        sheet.analyze(true);
    }

    public PropertiesStorage readConfig(ExcelWorkbook excelReader) {
        propertiesStorage = new PropertiesStorage();
        int counter = 0;
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            String property = PoiHelper.getValueAsString(sheet.getCell(row, propertyColumnDef));
            String value = PoiHelper.getValueAsString(sheet.getCell(row, valueColumnDef));
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

package de.micromata.merlin.smarthome.examples.openhab_knx;

import de.micromata.merlin.smarthome.examples.openhab_knx.data.ZoneminderThing;
import de.micromata.merlin.excel.ExcelSheet;
import de.micromata.merlin.excel.ExcelWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class ZoneminderThingsReader {
    private Logger log = LoggerFactory.getLogger(ZoneminderThingsReader.class);

    private static final String SHEET_NAME = "Zoneminder";

    public void readZoneminderThings(ExcelWorkbook excelWorkbook) {
        ExcelSheet sheet = excelWorkbook.getSheet(SHEET_NAME);
        int counter = 0;
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            ZoneminderThing thing = new ZoneminderThing();
            thing.setLabel(sheet.getCell(row, "label"));
            thing.setId(sheet.getCell(row, "id"));
            thing.setNumber(sheet.getCell(row, "number"));
            //DataStorage.getDefaultInstance().add(thing);
            counter++;
        }
        log.info("Number of read zoneminder monitors in sheet '" + SHEET_NAME + "': " + counter);
    }
}

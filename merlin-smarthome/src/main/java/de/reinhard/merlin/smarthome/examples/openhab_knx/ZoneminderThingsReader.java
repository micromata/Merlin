package de.reinhard.merlin.smarthome.examples.openhab_knx;

import de.reinhard.merlin.excel.ConfigReader;
import de.reinhard.merlin.excel.ExcelSheet;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.smarthome.examples.openhab_knx.data.DataStorage;
import de.reinhard.merlin.smarthome.examples.openhab_knx.data.ZoneminderThing;
import de.reinhard.merlin.velocity.VelocityHelper;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ZoneminderThingsReader {
    private Logger log = LoggerFactory.getLogger(ZoneminderThingsReader.class);

    private static final String SHEET_NAME = "Zoneminder";

    public void readZoneminderThings(ExcelWorkbook excelWorkbook) {
        ExcelSheet sheet = excelWorkbook.getSheet(SHEET_NAME);
        int counter = 0;
        while (sheet.hasNextRow()) {
            sheet.nextRow();
            ZoneminderThing thing = new ZoneminderThing();
            thing.setLabel(sheet.getCell("label"));
            thing.setId(sheet.getCell("id"));
            thing.setNumber(sheet.getCell("number"));
            //DataStorage.getInstance().add(thing);
            counter++;
        }
        log.info("Number of read zoneminder monitors in sheet '" + SHEET_NAME + "': " + counter);
    }
}

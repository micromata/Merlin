package de.reinhard.merlin.smarthome.examples.openhab_knx;

import de.reinhard.merlin.ResultMessage;
import de.reinhard.merlin.excel.ColumnValidator;
import de.reinhard.merlin.excel.ExcelSheet;
import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.smarthome.examples.openhab_knx.data.DataStorage;
import de.reinhard.merlin.smarthome.examples.openhab_knx.data.KnxThing;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class KnxThingsReader {
    private Logger log = LoggerFactory.getLogger(KnxThingsReader.class);

    private static final String SHEET_NAME = "KNX";

    public void readKNXThings(ExcelWorkbook excelReader) {
        ExcelSheet sheet = excelReader.getSheet(SHEET_NAME);
        sheet.getColumnDef("Id").addColumnListener(new ColumnValidator().setUnique().setRequired());
        sheet.getColumnDef("Device").addColumnListener(new ColumnValidator().setRequired());
        sheet.analyze(true);
        if (sheet.hasValidationErrors()) {
            for(ResultMessage msg : sheet.getValidationErrors()) {
                log.error(msg.getMessage());
            }
            log.error("*** Aborting processing of knx things due to validation errors (see above).");
            return;
        }

        int counter = 0;
        while (sheet.hasNextRow()) {
            sheet.nextRow();
            KnxThing thing = new KnxThing();
            sheet.readRow(thing);
            DataStorage.getInstance().add(thing);
            counter++;
        }
        log.info("Number of read KNX item in sheet '" + SHEET_NAME + "': " + counter);
    }
}

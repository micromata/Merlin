package de.micromata.merlin.smarthome.examples.openhab_knx;

import de.micromata.merlin.smarthome.examples.openhab_knx.data.DataStorage;
import de.micromata.merlin.smarthome.examples.openhab_knx.data.KnxThing;
import de.micromata.merlin.ResultMessage;
import de.micromata.merlin.excel.ExcelColumnValidator;
import de.micromata.merlin.excel.ExcelSheet;
import de.micromata.merlin.excel.ExcelWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class KnxThingsReader {
    private Logger log = LoggerFactory.getLogger(KnxThingsReader.class);

    private static final String SHEET_NAME = "KNX";

    public void readKNXThings(ExcelWorkbook excelReader) {
        ExcelSheet sheet = excelReader.getSheet(SHEET_NAME);
        sheet.registerColumn("Id", new ExcelColumnValidator().setUnique().setRequired());
        sheet.registerColumn("Device", new ExcelColumnValidator().setRequired());
        sheet.registerColumns("Type", "Icon", "KNX-Channel-Type", "Sitemap-Frame", "Group", "ga", "Label", "Format", "Persistency");
        sheet.analyze(true);
        if (sheet.hasValidationErrors()) {
            for(ResultMessage msg : sheet.getAllValidationErrors()) {
                log.error(msg.getMessage());
            }
            log.error("*** Aborting processing of knx things due to validation errors (see above).");
            return;
        }

        int counter = 0;
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            KnxThing thing = new KnxThing();
            sheet.readRow(row, thing);
            DataStorage.getInstance().add(thing);
            counter++;
        }
        log.info("Number of read KNX item in sheet '" + SHEET_NAME + "': " + counter);
    }
}

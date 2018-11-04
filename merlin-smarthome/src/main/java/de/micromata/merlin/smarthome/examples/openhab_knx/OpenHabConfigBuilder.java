package de.micromata.merlin.smarthome.examples.openhab_knx;

import de.micromata.merlin.smarthome.examples.openhab_knx.data.DataStorage;
import de.micromata.merlin.excel.ExcelConfigReader;
import de.micromata.merlin.excel.ExcelWorkbook;
import de.micromata.merlin.velocity.VelocityHelper;
import org.apache.velocity.VelocityContext;

import java.io.File;

public class OpenHabConfigBuilder {
    public static void main(String[] args) {
        new OpenHabConfigBuilder().run();
    }

    private ExcelWorkbook excelWorkbook;

    public void run() {
        File outDir = new File("merlin-smarthome/out/examples/openhab-knx");
        File configDir = createDir(outDir, "config");
        File itemsDir = createDir(configDir, "items");
        File persistenceDir = createDir(configDir, "persistence");
        File sitemapsDir = createDir(configDir, "sitemaps");
        File thingsDir = createDir(configDir, "things");
        excelWorkbook = new ExcelWorkbook("merlin-smarthome/examples/openhab-knx/OpenHab-KNX-Definitions.xlsx");
        new KnxThingsReader().readKNXThings(excelWorkbook);
        new ExcelConfigReader(excelWorkbook.getSheet("Config"), "Property", "Value")
                .readConfig(excelWorkbook);
        VelocityContext context = new VelocityContext();
        context.put("data", DataStorage.getInstance());
        File templateDir = new File("merlin-smarthome/examples/openhab-knx/");
        VelocityHelper.merge(templateDir, "knx.things", thingsDir, context);
        VelocityHelper.merge(templateDir, "knx.items", itemsDir, context);
        VelocityHelper.merge(templateDir, "zoneminder.things", thingsDir, context);
        VelocityHelper.merge(templateDir, "jdbc.persist", persistenceDir, context);
        VelocityHelper.merge(templateDir, "home.sitemap", sitemapsDir, context);
    }

    private File createDir(File path, String dir) {
        File subDir = new File(path, dir);
        if (subDir.exists() == false) {
            subDir.mkdirs();
        }
        return subDir;
    }
}
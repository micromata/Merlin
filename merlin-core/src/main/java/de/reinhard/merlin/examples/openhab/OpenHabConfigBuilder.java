package de.reinhard.merlin.examples.openhab;

import de.reinhard.merlin.excel.ConfigReader;
import de.reinhard.merlin.excel.ExcelWorkbook;

import java.io.File;

public class OpenHabConfigBuilder {
    public static void main(String[] args) {
        new OpenHabConfigBuilder().run();
    }

    private ExcelWorkbook workbook;

    public void run() {
        File outDir = new File("out");
        File configDir = createDir(outDir, "config");
        File itemsDir = createDir(configDir, "items");
        File persistenceDir = createDir(configDir, "persistence");
        File sitemapsDir = createDir(configDir, "sitemaps");
        File thingsDir = createDir(configDir, "things");
        workbook = new ExcelWorkbook("examples/OpenHab-KNX/OpenHab-KNX-Definitions.xlsx");
        new ConfigReader(workbook.getSheet("Config"), "Property", "Value").readConfig(workbook);
    }

    private File createDir(File path, String dir) {
        File subDir = new File(path, dir);
        if (subDir.exists() == false) {
            subDir.mkdirs();
        }
        return subDir;
    }
}
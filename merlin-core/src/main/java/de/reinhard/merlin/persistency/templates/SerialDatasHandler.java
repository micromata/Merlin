package de.reinhard.merlin.persistency.templates;

import de.reinhard.merlin.excel.ExcelWorkbook;
import de.reinhard.merlin.persistency.DirectoryWatchEntry;
import de.reinhard.merlin.persistency.FileDescriptor;
import de.reinhard.merlin.word.templating.SerialData;
import de.reinhard.merlin.word.templating.SerialDataExcelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

class SerialDatasHandler extends AbstractHandler<SerialData> {
    private Logger log = LoggerFactory.getLogger(SerialDatasHandler.class);

    SerialDatasHandler(DirectoryScanner directoryScanner) {
        super(directoryScanner, "SerialData");
        this.supportedFileExtensions = new String[]{"xls", "xlsx"};
    }

    @Override
    SerialData read(DirectoryWatchEntry watchEntry, Path path, FileDescriptor fileDescriptor) {
        ExcelWorkbook workbook = ExcelWorkbook.create(path);
        SerialDataExcelReader templateReader = new SerialDataExcelReader();
        if (!templateReader.isMerlinSerialTemplateData(workbook)) {
            return null;
        }
        SerialData serialData = templateReader.readFromWorkbook(workbook, null);
        if (serialData == null) {
            return null;
        }
        return serialData;
    }
}

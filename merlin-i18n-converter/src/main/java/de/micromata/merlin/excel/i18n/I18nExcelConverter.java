package de.micromata.merlin.excel.i18n;

import de.micromata.merlin.excel.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class I18nExcelConverter {
    private static Logger log = LoggerFactory.getLogger(I18nExcelConverter.class);

    @Getter
    private Dictionary dictionary;
    private CellStyle cellStyleHeadRow;
    private CellStyle cellStyleTranslation;
    private CellStyle cellStyleTranslationModified;
    private CellStyle cellStyleKey;

    public I18nExcelConverter() {
        this.dictionary = new Dictionary();
    }

    public I18nExcelConverter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * @param inputStream
     * @param workbookName Only for logging purposes.
     * @throws IOException
     */
    public void importTranslations(InputStream inputStream, String workbookName) throws IOException {
        ExcelWorkbook workbook = new ExcelWorkbook(inputStream, workbookName);
        ExcelSheet sheet = workbook.getSheet("Translations");
        if (sheet == null) {
            log.error("Can't read translations from Excel workbook '" + workbookName + "'. No sheet named 'translations' founde.");
            return;
        }
        sheet.registerColumn("key", new ExcelColumnValidator().setRequired());

        Set<String> languages = new TreeSet<>();
        Row headRow = sheet.getHeadRow();
        for (int cellnum = 1; cellnum < headRow.getLastCellNum(); cellnum++) {
            String lang = PoiHelper.getValueAsString(headRow.getCell(cellnum)).trim();
            if (lang.length() > 3 || !StringUtils.isAlpha(lang)) {
                log.error("Ignoring column named '" + lang + "'. It's not seemed to be a language column.");
                continue;
            }
            languages.add(lang);
            sheet.registerColumn(lang, new ExcelColumnValidator());
        }
        sheet.reset();
        Iterator<Row> it = sheet.getDataRowIterator();
        while (it.hasNext()) {
            Row row = it.next();
            String key = sheet.getCellString(row, "key");
            for (String lang : languages) {
                String translation = sheet.getCellString(row, lang);
                dictionary.addTranslation(lang, key, translation);
            }
        }
        workbook.close();
    }

    public void write(OutputStream outputStream) throws IOException {
        Workbook poiWorkbook = new XSSFWorkbook();
        ExcelWorkbook workbook = new ExcelWorkbook(poiWorkbook);
        createStyles(workbook);
        ExcelSheet sheet = workbook.createOrGetSheet("Translations");
        ExcelRow row = sheet.createRow();
        row.createCells(cellStyleHeadRow, "key");
        sheet.setColumnWidth(0, 10000);
        int col = 0;
        for (String lang : dictionary.getUsedLangs()) {
            sheet.setColumnWidth(++col, 20000);
            row.createCells(cellStyleHeadRow, lang);
        }
        row.setHeight(20);

        int rows = 1;
        for (String key : dictionary.getKeys()) {
            row = sheet.createRow();
            rows++;
            row.createCells(cellStyleKey, key);
            TranslationEntry entry = dictionary.getEntry(key);
            if (entry == null) continue; // Shouldn't occur.
            for (String lang : dictionary.getUsedLangs()) {
                if (dictionary.isModified(lang, key)) {
                    row.createCells(cellStyleTranslationModified, StringUtils.defaultString(entry.getTranslation(lang)));
                    row.getRow().getCell(0).setCellStyle(cellStyleTranslationModified);
                } else {
                    row.createCells(cellStyleTranslation, StringUtils.defaultString(entry.getTranslation(lang)));
                }
            }
        }
        sheet.getPoiSheet().setAutoFilter(new CellRangeAddress(0, rows, 0, col));

        if (this.dictionary.getDiffDictionary() != null) {
            for (String lang : this.dictionary.getDiffDictionary().getUsedLangs()) {
                SortedSet<TranslationDiffEntry> diffs = dictionary.getDifferences(lang);
                if (diffs.size() == 0) {
                    log.info("No differences found for lang '" + lang + "'.");
                    continue;
                }
                log.info("Found " + diffs.size() + " differing entries for lang '" + lang + "'.");
                sheet = workbook.createOrGetSheet(lang + " diffs");
                row = sheet.createRow();
                row.createCells(cellStyleHeadRow, "key", "this", "other");
                row.setHeight(20);
                sheet.setColumnWidth(0, 10000);
                sheet.setColumnWidth(1, 20000);
                sheet.setColumnWidth(2, 20000);
                rows = 1;
                for (TranslationDiffEntry diffEntry : diffs) {
                    row = sheet.createRow();
                    rows++;
                    row.createCells(cellStyleKey, diffEntry.getI18nKey());
                    row.createCells(cellStyleTranslation, diffEntry.getThisValue(), diffEntry.getOtherValue());
                }
                sheet.getPoiSheet().setAutoFilter(new CellRangeAddress(0, rows, 0, 2));
            }
        }
        workbook.getPOIWorkbook().write(outputStream);
        workbook.close();
    }

    private void createStyles(ExcelWorkbook workbook) {
        cellStyleHeadRow = workbook.getPOIWorkbook().createCellStyle();
        Font font = workbook.getPOIWorkbook().createFont();
        font.setColor(IndexedColors.ROYAL_BLUE.index);
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        cellStyleHeadRow.setFont(font);

        cellStyleTranslation = workbook.getPOIWorkbook().createCellStyle();
        cellStyleTranslation.setWrapText(true);
        cellStyleTranslation.setVerticalAlignment(VerticalAlignment.TOP);

        cellStyleTranslationModified = workbook.getPOIWorkbook().createCellStyle();
        cellStyleTranslationModified.setWrapText(true);
        cellStyleTranslationModified.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyleTranslationModified.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        cellStyleTranslationModified.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cellStyleKey = workbook.getPOIWorkbook().createCellStyle();
        cellStyleKey.setVerticalAlignment(VerticalAlignment.TOP);
    }
}

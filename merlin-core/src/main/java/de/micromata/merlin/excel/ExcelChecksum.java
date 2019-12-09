package de.micromata.merlin.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 * Helper for building checksums of Excel workbooks. Can be used to detect modifications of Excel files.
 * Checksums will be written as custom document property.
 */
public class ExcelChecksum {
    private static Logger log = LoggerFactory.getLogger(ExcelChecksum.class);
    private static final String CUSTOM_PROPERTY_CHECKSUM = "MerlinChecksum";

    /**
     * @param workbook The workbook to calculate the checksum for.
     * @return checksum.
     */
    public static long buildChecksum(Workbook workbook) {
        Adler32 cs = new Adler32();
        for (Sheet sheet : workbook) {
            update(cs, sheet.getSheetName());
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String str = StringUtils.defaultString(cell.getStringCellValue());
                    update(cs, str);
                }
            }
        }
        return cs.getValue();
    }

    private static void update(Checksum cs, String value) {
        byte[] bytes = value.getBytes();
        if (value != null) cs.update(bytes, 0, bytes.length);
        else cs.update(0);
    }

    public static void writeChecksum(Workbook workbook, long checksum) {
        if (workbook instanceof XSSFWorkbook) {
            POIXMLProperties.CustomProperties custProp = getCustomProperties((XSSFWorkbook) workbook);
            if (custProp == null) {
                return;
            }
            custProp.addProperty(CUSTOM_PROPERTY_CHECKSUM, String.valueOf(checksum));

        } else if (workbook instanceof HSSFWorkbook) {
            SummaryInformation info = ((HSSFWorkbook) workbook).getSummaryInformation();
            if (info == null) {
                ((HSSFWorkbook) workbook).createInformationProperties();
                info = ((HSSFWorkbook) workbook).getSummaryInformation();
            }
            if (info == null) {
                log.error("SummaryInformation not given for HSSFWorkbook. Can't write checksum.");
                return;
            }
            info.setComments(CUSTOM_PROPERTY_CHECKSUM + "=" + checksum);
        } else {
            log.error("Excel document type '" + workbook.getClass().getName() + "' isn't supported.");
        }
    }

    private static POIXMLProperties.CustomProperties getCustomProperties(XSSFWorkbook xssfWorkbook) {
        POIXMLProperties props = xssfWorkbook.getProperties();
        if (props == null) {
            log.error("POIXMLProperties not given for XSSFWorkbook. Can't handle checksum.");
            return null;
        }
        POIXMLProperties.CustomProperties custProp = props.getCustomProperties();
        if (custProp == null) {
            log.error("POIXMLProperties.CustomProperties not given for XSSFWorkbook. Can't handle checksum.");
            return null;
        }
        return custProp;
    }

    public static long readChecksum(Workbook workbook) {
        if (workbook instanceof XSSFWorkbook) {
            POIXMLProperties.CustomProperties custProp = getCustomProperties((XSSFWorkbook) workbook);
            if (custProp == null) {
                return -1;
            }
            CTProperty prop = custProp.getProperty(CUSTOM_PROPERTY_CHECKSUM);
            if (prop == null) {
                return -1;
            }
            String value = prop.getLpwstr();
            if (value == null) {
                return -1;
            }
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ex) {
                log.error("Can't parse checksum, isn't a number: " + value);
                return -1;
            }
        } else if (workbook instanceof HSSFWorkbook) {
            SummaryInformation info = ((HSSFWorkbook) workbook).getSummaryInformation();
            if (info == null) {
                return -1;
            }
            String comments = info.getComments();
            if (comments != null && comments.startsWith(CUSTOM_PROPERTY_CHECKSUM + "=") &&
                    comments.length() > CUSTOM_PROPERTY_CHECKSUM.length() + 1) {
                String value = comments.substring(comments.indexOf('=') + 1);
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    log.error("Can't parse checksum, isn't a number: " + value);
                    return -1;
                }
            }
            return -1;
        } else {
            log.error("Excel document type '" + workbook.getClass().getName() + "' isn't supported.");
        }
        return -1;
    }
}


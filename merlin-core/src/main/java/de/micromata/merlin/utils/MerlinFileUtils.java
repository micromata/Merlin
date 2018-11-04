package de.micromata.merlin.utils;

import org.apache.commons.io.FileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MerlinFileUtils {
    private static SimpleDateFormat ISO_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String getISODate() {
        synchronized (ISO_DATEFORMAT) {
            return ISO_DATEFORMAT.format(new Date());
        }
    }

    public static String getByteCountToDisplaySize(Long length) {
        if (length == null) {
            return "0KB";
        }
        return FileUtils.byteCountToDisplaySize(length);
    }

    public static String getByteCountToDisplaySize(int length) {
        return FileUtils.byteCountToDisplaySize(length);
    }
}

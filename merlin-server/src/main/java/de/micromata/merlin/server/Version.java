package de.micromata.merlin.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class Version {
    private Logger log = LoggerFactory.getLogger(Version.class);
    private String appName;
    private String version;
    private String buildDateUTC;
    private Date buildDate;
    private String updateVersion;

    private static final Version instance = new Version();

    public static Version getInstance() {
        instance.init();
        return instance;
    }

    private void init() {
        synchronized (this) {
            if (appName != null) {
                return;
            }
            try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("version.properties")) {
                if (inputStream == null) {
                    log.warn("version.properties not found (OK, if started e. g. in IDE");
                    version = "99.0";
                    appName = "Merlin";
                    buildDate = new Date();
                    return;
                } else {
                    Properties props = new Properties();
                    props.load(inputStream);
                    appName = props.getProperty("name");
                    version = props.getProperty("version");
                    String buildDateMillisString = props.getProperty("build.date.millis");
                    long buildDateMillis = 0;
                    if (buildDateMillisString != null) {
                        try {
                            buildDateMillis = Long.parseLong(buildDateMillisString);
                        } catch (NumberFormatException ex) {
                            log.error("Can't parse build date (millis expected): " + buildDateMillisString + ": " + ex.getMessage(), ex);
                        }
                    }
                    buildDate = new Date(buildDateMillis);
                }
            } catch (Exception ex) {
                log.error("Can't load version information from classpath. File 'version.properties' not found: " + ex.getMessage(), ex);
                appName = "Merlin";
                version = "?.?";
                buildDateUTC = "1970-01-01 00:00:00";
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            buildDateUTC = formatter.format(buildDate);
            log.debug("appName=" + appName + ", version=" + version + ", buildDateUTC=" + buildDateUTC);
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getVersion() {
        return version;
    }

    /**
     * Replaces -SNAPSHOT by dev for snapshot versions. For none snapshot releases the returned value is equal to the version.
     *
     * @return the version as string.
     */
    public String getShortVersion() {
        return version.replace("-SNAPSHOT", "dev");
    }

    public String getBuildDateUTC() {
        return buildDateUTC;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    /**
     * @return Version of the available update, if exist. Otherwise null.
     */
    public String getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(String updateVersion) {
        this.updateVersion = updateVersion;
    }
}

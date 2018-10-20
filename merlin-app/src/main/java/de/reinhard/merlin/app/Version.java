package de.reinhard.merlin.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
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
                Properties props = new Properties();
                props.load(inputStream);
                appName = props.getProperty("name");
                version = props.getProperty("version");
                buildDateUTC = props.getProperty("build.date.utc");

            } catch (IOException ex) {
                log.error("Can't load version information from classpath. File 'version.properties' not found: " + ex.getMessage(), ex);
                appName = "Merlin";
                version = "?.?";
                buildDateUTC = "1970-01-01 00:00:00";
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                buildDate = formatter.parse(buildDateUTC);
            } catch (ParseException ex) {
                log.error("Can't parse date string '" + buildDateUTC + "' of version file: "+ ex.getMessage(), ex);
                buildDate = new Date(0);
            }
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getVersion() {
        return version;
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

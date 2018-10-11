package de.reinhard.merlin.app.javafx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RunningMode {
    private static Logger log = LoggerFactory.getLogger(RunningMode.class);
    private static OS_TYPE osType;

    public enum Mode {TemplatesTest}

    public enum OS_TYPE {MAC_OS, WINDOWS, LINUX, OTHER}

    private static boolean running;
    private static File baseDir;
    private static Boolean development;

    public static Mode getMode() {
        return Mode.TemplatesTest;
    }

    public static boolean isDevelopmentMode() {
        if (development == null) {
            development = new File("merlin-core").exists() || new File("../merlin-core").exists();
            if (development) {
                log.info("Starting Merlin server in development mode.");
            }
        }
        return development;
    }

    public static OS_TYPE getOSType() {
        if (osType == null) {
            String osTypeString = System.getProperty("os.name");
            if (osTypeString == null) {
                osType = OS_TYPE.OTHER;
            } else if (osTypeString.toLowerCase().contains("mac")) {
                osType = OS_TYPE.MAC_OS;
            } else if (osTypeString.toLowerCase().contains("win")) {
                osType = OS_TYPE.WINDOWS;
            } else if (osTypeString.toLowerCase().contains("linux")) {
                osType = OS_TYPE.LINUX;
            } else {
                osType = OS_TYPE.OTHER;
            }
        }
        return osType;
    }

    public static boolean isRunning() {
        return running;
    }

    static void setRunning(boolean running) {
        RunningMode.running = running;
    }

    public static File getBaseDir() {
        if (baseDir == null) {
            baseDir = new File(System.getProperty("user.dir")); // Merlin base dir.
        }
        return null;
    }
}

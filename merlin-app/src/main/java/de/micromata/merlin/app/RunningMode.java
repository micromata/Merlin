package de.micromata.merlin.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RunningMode {
    private static Logger log = LoggerFactory.getLogger(RunningMode.class);
    private static OSType osType;

    public enum Mode {PRODUCTION, DEVELOPMENT};

    public enum ServerType {DESKTOP, SERVER};

    public enum OSType {MAC_OS, WINDOWS, LINUX, OTHER};

    private static boolean running;
    private static File baseDir;
    private static Boolean development;
    private static ServerType serverType;

    public static Mode getMode() {
        return isDevelopmentMode() ? Mode.DEVELOPMENT : Mode.PRODUCTION;
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

    public static OSType getOSType() {
        if (osType == null) {
            String osTypeString = System.getProperty("os.name");
            if (osTypeString == null) {
                osType = OSType.OTHER;
            } else if (osTypeString.toLowerCase().contains("mac")) {
                osType = OSType.MAC_OS;
            } else if (osTypeString.toLowerCase().contains("win")) {
                osType = OSType.WINDOWS;
            } else if (osTypeString.toLowerCase().contains("linux")) {
                osType = OSType.LINUX;
            } else {
                osType = OSType.OTHER;
            }
        }
        return osType;
    }

    public static ServerType getServerType() {
        return serverType;
    }

    public static void setServerType(ServerType serverType) {
        log.info("Starting server as type: " + serverType);
        RunningMode.serverType = serverType;
    }

    public static File getBaseDir() {
        if (baseDir == null) {
            baseDir = new File(System.getProperty("user.dir")); // Merlin base dir.
        }
        return null;
    }
}

package de.reinhard.merlin.app.javafx;

public class RunningMode {
    private static OS_TYPE osType;

    public enum Mode {TemplatesTest}

    public enum OS_TYPE {MAC_OS, WINDOWS, LINUX, OTHER}

    private static boolean running;

    public static Mode getMode() {
        return Mode.TemplatesTest;
    }

    public static boolean isDevelopmentMode() {
        return true;
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
}

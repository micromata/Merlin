package de.reinhard.merlin.app.javafx;

public class RunningMode {
    enum Mode {TemplatesTest}

    enum OperatingSystem {MacOS, Windows, Other}

    public static Mode getMode() {
        return Mode.TemplatesTest;
    }

    public static OperatingSystem getOperationSystem() {
        return OperatingSystem.MacOS;
    }

    public static boolean isMacOS() {
        return getOperationSystem() == OperatingSystem.MacOS;
    }
}

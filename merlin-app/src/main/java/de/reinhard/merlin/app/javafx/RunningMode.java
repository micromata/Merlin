package de.reinhard.merlin.app.javafx;

public class RunningMode {
    public enum Mode {TemplatesTest}

    public static Mode getMode() {
        return Mode.TemplatesTest;
    }

    public static boolean isDevelopmentMode() {
        return true;
    }
}

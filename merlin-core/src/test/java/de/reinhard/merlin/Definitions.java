package de.reinhard.merlin;

import java.io.File;

public class Definitions {
    public static final File EXAMPLES_EXCEL_TEST_DIR = new File("../examples/Excel");
    public static final File EXAMPLES_TEMPLATES_TEST_DIR = new File("../examples/templates");

    public static final File OUTPUT_DIR = new File("out");

    static {
        if (!OUTPUT_DIR.exists() && !OUTPUT_DIR.mkdirs()) {
            throw new RuntimeException("Can't create test output directory '" + OUTPUT_DIR.getAbsolutePath() + "'");
        }
    }
}

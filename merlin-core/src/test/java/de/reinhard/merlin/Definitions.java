package de.reinhard.merlin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Definitions {
    public static final File EXAMPLES_TEST_DIR = new File("examples/tests");

    public static final File OUTPUT_DIR = new File("out");

    static {
        if (!OUTPUT_DIR.exists() && !OUTPUT_DIR.mkdirs()) {
            throw new RuntimeException("Can't create test output directory '" + OUTPUT_DIR.getAbsolutePath() + "'");
        }
    }
}

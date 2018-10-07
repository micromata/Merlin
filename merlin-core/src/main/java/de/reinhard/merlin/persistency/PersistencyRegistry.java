package de.reinhard.merlin.persistency;

import de.reinhard.merlin.persistency.filesystem.FileSystemPersistency;

import java.nio.file.Path;

/**
 * Default persistency for files (such as template files and template definition files is {@link FileSystemPersistency}.
 * You may implement and register your own (e. g. for handling files in a database etc.).
 */
public class PersistencyRegistry {
    private static final PersistencyRegistry instance = new PersistencyRegistry();

    private PersistencyInterface persistency;

    public static PersistencyRegistry getInstance() {
        return instance;
    }

    public static PersistencyInterface getDefault() {
        return instance.getPersistency();
    }

    public PersistencyInterface getPersistency() {
        return persistency;
    }

    public void setPersistency(PersistencyInterface persistency) {
        this.persistency = persistency;
    }

    public AbstractDirectoryWatcher newInstance(Path root, String... fileExtensions) {
        return instance.newInstance(root, fileExtensions);
    }

    private PersistencyRegistry() {
        this.persistency = new FileSystemPersistency();
    }
}

package de.reinhard.merlin.persistency;

import java.nio.file.Path;

public interface DirectoryListener {
    public void directoryEvent(Path root, Path path, long lastModified, ModificationType type);

    public void fileEvent(Path root, Path path, long lastModified, ModificationType type);
}

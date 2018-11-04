package de.micromata.merlin.app;

public class ConfigurationTemplatesDir {
    private String directory;
    private boolean recursive;

    public String getDirectory() {
        return directory;
    }

    public ConfigurationTemplatesDir setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public boolean isRecursive() {
        return recursive;
    }

    /**
     *
     * @param recursive If true, the directory will be scanned recursively for templates, otherwise only the given
     *                  directory is scanned without looking at the sub directories. Default is false.
     * @return this for chaining.
     */
    public ConfigurationTemplatesDir setRecursive(boolean recursive) {
        this.recursive = recursive;
        return this;
    }
}

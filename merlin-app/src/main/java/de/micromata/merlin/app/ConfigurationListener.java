package de.reinhard.merlin.app;

public interface ConfigurationListener {
    /**
     * Called by ConfigurationHandler if Configuration is modified and saved.
     */
    public void templatesDirsModified();
}

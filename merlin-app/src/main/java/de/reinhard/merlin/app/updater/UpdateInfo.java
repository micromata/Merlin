package de.reinhard.merlin.app.updater;

import de.reinhard.merlin.app.Version;
import de.reinhard.merlin.app.javafx.RunningMode;

public class UpdateInfo {
    private String version;
    private String baseUrl;
    private String installerUrl;
    private String fileSize;
    private String filename;
    private String comment;

    private static final UpdateInfo instance = new UpdateInfo();

    public static UpdateInfo getInstance() {
        return instance;
    }

    private UpdateInfo() {
    }

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
        Version.getInstance().setUpdateVersion(version);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getInstallerUrl() {
        return installerUrl;
    }

    void setInstallerUrl(String installerUrl) {
        this.installerUrl = installerUrl;
    }

    public String getFileSize() {
        return fileSize;
    }

    void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilename() {
        return filename;
    }

    void setFilename(String filename) {
        this.filename = filename;
    }

    public String getComment() {
        return comment;
    }

    void setComment(String comment) {
        this.comment = comment;
    }

    public void setDevelopmentTestData() {
        if (!RunningMode.isDevelopmentMode()) {
            throw new IllegalStateException("Don't call setDevelopmentTestData() outside the development mode!");
        }
        // No update mechanism in development mode.
        UpdateInfo updateInfo = UpdateInfo.getInstance();
        updateInfo.setVersion("10.0");
        updateInfo.setInstallerUrl("https://github.com/kreinhard/merlin/releases/download/v0.2/merlin_macos_0_2.dmg");
        updateInfo.setFileSize("67.2MB");
        updateInfo.setFilename("merlin_macos_0_2.dmg");
        updateInfo.setComment("Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat.");
    }
}
package de.reinhard.merlin.app.updater;

import com.install4j.api.context.UserCanceledException;
import com.install4j.api.launcher.ApplicationLauncher;
import com.install4j.api.launcher.Variables;
import com.install4j.api.update.ApplicationDisplayMode;
import com.install4j.api.update.UpdateChecker;
import com.install4j.api.update.UpdateDescriptor;
import com.install4j.api.update.UpdateDescriptorEntry;
import de.reinhard.merlin.app.Version;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AppUpdater {
    private static final String APPLICATION_ID = "5115-4774-2533-4349";
    private static Logger log = LoggerFactory.getLogger(AppUpdater.class);
    private static final AppUpdater instance = new AppUpdater();

    public static AppUpdater getInstance() {
        return instance;
    }

    private UpdateDescriptorEntry validUpdateDescriptorEntry;

    public void checkUpdate() {
        CompletableFuture<UpdateDescriptorEntry> future = new CompletableFuture<>();
        try {
            getUpdateDescriptor(future);
        } catch (Exception ex) {
            log.error("Can't update current application: " + ex.getMessage(), ex);
        }
        UpdateDescriptorEntry updateDescriptorEntry = null;
        try {
            updateDescriptorEntry = future.get(); // wait for future to be assigned a result and retrieve it
        } catch (InterruptedException | ExecutionException ex) {
            log.error("While waiting for file browser: " + ex.getMessage(), ex);
        }
        if (updateDescriptorEntry == null) {
            log.info("No updates found (OK).");
            return;
        }
        // only installers and single bundle archives on macOS are supported for background updates
        if (updateDescriptorEntry.isArchive() && !updateDescriptorEntry.isSingleBundle()) {
            log.info("Only installers and single bundle archives on macOS are supported for background update (can't update):" + updateDescriptorEntry.getURL());
            return;
        }
        validUpdateDescriptorEntry = updateDescriptorEntry;
        log.info("A new version " + updateDescriptorEntry.getNewVersion() + " is available for update: " + updateDescriptorEntry.getFileName()
                + ". Url=" + updateDescriptorEntry.getURL());
        Version.getInstance().setUpdateVersion(updateDescriptorEntry.getNewVersion());
    }

    public boolean install() {
        if (validUpdateDescriptorEntry == null) {
            log.info("Nothing to install. No valid update available.");
            return false;
        }
        try {
            log.info("Launching updater on local desktop.");
            ApplicationLauncher.launchApplication("428", null, false, new ApplicationLauncher.Callback() {
                        public void exited(int exitValue) {
                            log.info("Launcher exited.");
                        }

                        public void prepareShutdown() {
                            log.info("Shutdown in progress.");
                        }
                    }
            );
        } catch (IOException ex) {
            log.error("Error while updating: " + ex.getMessage(), ex);
        }
        return true;
    }

    private void getUpdateDescriptor(CompletableFuture<UpdateDescriptorEntry> future) {

        // The compiler variable sys.updatesUrl holds the URL where the updates.xml file is hosted.
        // That URL is defined on the "Installer->Auto Update Options" step.
        // The same compiler variable is used by the "Check for update" actions that are contained in the update
        // downloaders.
        String updateUrl;
        try {
            updateUrl = Variables.getCompilerVariable("sys.updatesUrl");
        } catch (IOException ex) {
            log.error("Can't check update url: " + ex.getMessage(), ex);
            return;
        }
        log.info("Checking update: " + updateUrl);
        UpdateDescriptor updateDescriptor;
        try {
            updateDescriptor = UpdateChecker.getUpdateDescriptor(updateUrl, ApplicationDisplayMode.UNATTENDED);
        } catch (UserCanceledException | IOException ex) {
            log.error("Can't get updates: " + ex.getMessage(), ex);
            return;
        }
        //log.info("UpdateDescriptor base url: " + updateDescriptor.getBaseUrl());
        /*UpdateDescriptorEntry[] entries = updateDescriptor.getEntries();
        if (entries == null) {
            log.info("No update entries found.");
        } else {
            for (UpdateDescriptorEntry entry : entries) {
                log.info("Update entry: "+ entry.getFileName());
            }
        }*/
        // If getPossibleUpdateEntry returns a non-null value, the version number in the updates.xml file
        // is greater than the version number of the local installation.
        future.complete(updateDescriptor.getPossibleUpdateEntry());
    }

    private Alert alert(String msg, Alert.AlertType type) {
        if (type == Alert.AlertType.ERROR) {
            log.error("Error alert: " + msg);
        } else {
            log.info("Show dialog: " + msg);
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Software update");
        alert.setContentText(msg);
        return alert;
    }
}

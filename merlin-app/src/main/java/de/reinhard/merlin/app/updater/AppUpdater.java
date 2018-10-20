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
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
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
        if (!validUpdateDescriptorEntry.isDownloaded()) {
            log.info("First, downloading update...");
            downloadAndUpdate();
        } else if (UpdateChecker.isUpdateScheduled()) {
            log.info("Update is ready...");
            executeUpdate();
        }
        return true;
    }

    private void downloadAndUpdate() {
        // Here the background update downloader is launched in the background
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                log.info("Launching update application...");
                // Note the third argument which makes the call to the background updater blocking.
                ApplicationLauncher.launchApplication(APPLICATION_ID, null, true, null);
                // At this point the update downloader has returned and we can check if the "Schedule update installation"
                // action has registered an update installer for execution
                // We now switch to the EDT in done() for terminating the application
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // rethrow exceptions that occurred in doInBackground() wrapped in an ExecutionException
                    if (!UpdateChecker.isUpdateScheduled()) {
                        Alert alert = alert("Software update failed. Update could not be downloaded.", Alert.AlertType.ERROR);
                        alert.showAndWait();
                        return;
                    }
                    Alert alert = alert("Merlin is now ready for updating. Do you like to proceed (recommended)?", Alert.AlertType.CONFIRMATION);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() != ButtonType.OK) {
                        return;
                    }
                    // We execute the update immediately, but you could ask the user whether the update should be
                    // installed now. The scheduling of update installers is persistent, so this will also work
                    // after a restart of the launcher.
                    executeUpdate();
                } catch (InterruptedException ex) {
                    log.error("Update interrupted: " + ex.getMessage(), ex);
                } catch (ExecutionException ex) {
                    log.error("Update error: " + ex.getMessage(), ex);
                    Alert alert = alert("Software update failed. An error has occured: " + ex.getMessage(), Alert.AlertType.ERROR);
                    alert.showAndWait();
                    return;
                }
            }
        }.execute();
    }

    private void executeUpdate() {
        log.info("Executing scheduled update.");
        UpdateChecker.executeScheduledUpdate(Arrays.asList("-q", "-splash", "Updating Hello World GUI ..."), true, null);
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

package de.reinhard.merlin.app.updater;

import com.install4j.api.context.UserCanceledException;
import com.install4j.api.launcher.Variables;
import com.install4j.api.update.ApplicationDisplayMode;
import com.install4j.api.update.UpdateChecker;
import com.install4j.api.update.UpdateDescriptor;
import com.install4j.api.update.UpdateDescriptorEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AppUpdater {
    private static Logger log = LoggerFactory.getLogger(AppUpdater.class);

    public static void update() {
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
        if (updateDescriptorEntry.isArchive()) {
            log.info("Found update is an archive (can't update):" + updateDescriptorEntry.getURL());
            return;
        }
        if (updateDescriptorEntry.isSingleBundle()) {
            log.info("Found update is a single bundle (can't update):" + updateDescriptorEntry.getURL());
            return;
        }
        if (!updateDescriptorEntry.isDownloaded()) {
    /*        // An update is available for download, so we add an update notice panel at the top of the window
            addUpdateNotice(updateDescriptorEntry, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    button.setText("Download in progress ...");
                    button.setEnabled(false);
                    downloadAndUpdate();
                }
            });
        } else if (UpdateChecker.isUpdateScheduled()) {
            // The update has been downloaded, but installation did not succeed yet.
            // When the user clicks the button we will execute the update directly
            addUpdateNotice(updateDescriptorEntry, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    executeUpdate();
                }
            });*/
        }
        log.info("Update found: " + updateDescriptorEntry.getURL());
    }

    private static void getUpdateDescriptor(CompletableFuture<UpdateDescriptorEntry> future) {

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
}

package de.reinhard.merlin.app.javafx;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class FileBrowser {
    private Logger log = LoggerFactory.getLogger(FileBrowser.class);
    private static FileBrowser instance;

    public enum SelectFilter {EXCEL, WORD, ALL, DIRECTORY}

    private File lastDir;
    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;

    public static FileBrowser getInstance() {
        if (instance == null) {
            instance = new FileBrowser();
        }
        return instance;
    }

    private FileBrowser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Merlin");
        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Merlin");
    }

    /**
     * @param file File or directory. If file then parent directory is used. Null-safe.
     */
    public void setLastDir(File file) {
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            lastDir = file;
        } else {
            lastDir = file.getParentFile();
        }
    }

    public void open(SelectFilter filter, CompletableFuture<File> future) {
        open(filter, (File) null, future);
    }

    public void open(SelectFilter filter, String initialDirectory, CompletableFuture<File> future) {
        open(filter, new File(initialDirectory), future);
    }

    public synchronized void open(SelectFilter filter, File initialDirectory, CompletableFuture<File> future) {
        if (initialDirectory == null || !initialDirectory.isDirectory()) {
            initialDirectory = lastDir;
        }
        if (initialDirectory == null || !initialDirectory.isDirectory()) {
            initialDirectory = new File(System.getProperty("user.home"));
        }
        addExtensionFilter(filter);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (filter == SelectFilter.DIRECTORY) {
                    future.complete(directoryChooser.showDialog(Main.getInstance().getStage()));
                } else {
                    future.complete(fileChooser.showOpenDialog(Main.getInstance().getStage()));
                }
            }
        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.getInstance().getStage().toFront();
            }
        });
    }

    private void addExtensionFilter(SelectFilter filter) {
        fileChooser.getExtensionFilters().clear();
        switch (filter) {
            case ALL:
                break;
            case WORD:
                fileChooser.getExtensionFilters().addAll(//
                        new FileChooser.ExtensionFilter("All Files", "*.*"),
                        new FileChooser.ExtensionFilter("Word", "*.docx"));
                break;
            case EXCEL:
                fileChooser.getExtensionFilters().addAll(//
                        new FileChooser.ExtensionFilter("All Files", "*.*"),
                        new FileChooser.ExtensionFilter("Excel", "*.xlsx", "*.xls"));
                break;
        }
    }
}

package de.reinhard.merlin.app.javafx;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class FileSystemBrowser {
    private Logger log = LoggerFactory.getLogger(FileSystemBrowser.class);
    private static FileSystemBrowser instance;

    public enum SelectFilter {
        EXCEL, WORD, ALL, DIRECTORY
    }

    private File lastDir;
    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;

    public static FileSystemBrowser getInstance() {
        if (instance == null) {
            instance = new FileSystemBrowser();
        }
        return instance;
    }

    private FileSystemBrowser() {
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
        lastDir = getDirectory(file);
    }

    public static File getDirectory(File file) {
        if (file.isDirectory()) {
            return file;
        } else {
            return file.getParentFile();
        }
    }

    public static SelectFilter getFilter(String filter) {
        if (filter == null) {
            return SelectFilter.ALL;
        }
        filter = filter.toLowerCase();
        if (filter.contains("dir")) {
            return SelectFilter.DIRECTORY;
        }
        if (filter.contains("excel") || filter.contains("xls")) {
            return SelectFilter.EXCEL;
        }
        if (filter.contains("word") || filter.contains("doc")) {
            return SelectFilter.WORD;
        }
        return SelectFilter.ALL;
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
                        new FileChooser.ExtensionFilter("Word (*.docx)", "*.docx"),
                        new FileChooser.ExtensionFilter("*.*", "*.*"));
                break;
            case EXCEL:
                fileChooser.getExtensionFilters().addAll(//
                        new FileChooser.ExtensionFilter("Excel (*.xlsx, *.xls)", "*.xlsx", "*.xls"),
                        new FileChooser.ExtensionFilter("*.*", "*.*"));
                break;
        }
    }
}

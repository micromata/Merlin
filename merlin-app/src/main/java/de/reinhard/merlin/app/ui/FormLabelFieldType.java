package de.reinhard.merlin.app.ui;

import javax.servlet.http.HttpServletRequest;

/**
 * The value type of a form field.
 */
public enum FormEntryType {
    STRING, INTEGER, FLOAT, CHECKED,
    /**
     * Represents a directory (path) string with a browse button. The browse button
     * should call the rest service: {@link de.reinhard.merlin.app.rest.FilesServiceRest#browseLocalFilesystem(HttpServletRequest, String, String)}
     */
    DIRECTORY;
}

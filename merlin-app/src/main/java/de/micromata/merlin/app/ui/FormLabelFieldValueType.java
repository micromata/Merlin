package de.micromata.merlin.app.ui;

import de.micromata.merlin.app.rest.FilesServiceRest;

import javax.servlet.http.HttpServletRequest;

/**
 * The value type of a form field.
 */
public enum FormLabelFieldValueType {
    STRING, INTEGER, FLOAT, CHECKED,
    /**
     * Represents a directory (path) string with a browse button. The browse button
     * should call the rest service: {@link FilesServiceRest#browseLocalFilesystem(HttpServletRequest, String, String)}
     */
    DIRECTORY;
}

package de.micromata.merlin.excel.i18n;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

class I18nJsonImportEntry {
    @JsonProperty
    Map<String, String> value;
    @JsonProperty("default")
    String defaultKey;
}

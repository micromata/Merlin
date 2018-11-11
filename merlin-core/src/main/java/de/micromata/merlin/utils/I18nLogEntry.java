package de.micromata.merlin.utils;

import de.micromata.merlin.csv.CSVParser;

import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class I18nLogEntry {
    private static final Pattern I18N_KEY_PATTERH = Pattern.compile("i18n=\"([^\"]*)\"");
    private static final Pattern ARGS_PATTERH = Pattern.compile("args=\\[([^\\]]*)\\]");

    private String i18nKey;
    private String[] args;

    public static I18nLogEntry parse(String str) {
        I18nLogEntry entry = new I18nLogEntry();
        Matcher matcher = I18N_KEY_PATTERH.matcher(str);
        if (matcher.find()) {
            entry.i18nKey = matcher.group(1);
        }
        matcher = ARGS_PATTERH.matcher(str);
        if (matcher.find()) {
            String argsString = matcher.group(1);
            List<String> list = new CSVParser(new StringReader(argsString)).setCsvSeparatorChar(',').parseLine();
            entry.args = list.stream().toArray(String[]::new);
        }
        return entry;
    }

    public I18nLogEntry() {
    }

    public I18nLogEntry(String i18nKey, Object... args) {
        this.i18nKey = i18nKey;
        if (args != null && args.length > 0) {
            this.args = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                Object val = args[i];
                this.args[i] = val != null ? String.valueOf(val) : null;
            }
        }
    }

    public String getI18nKey() {
        return i18nKey;
    }

    public void setI18nKey(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("i18n=\"").append(i18nKey).append("\"");
        if (args == null || args.length == 0) {
            return sb.toString();
        }
        sb.append(", args=[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(escapeValue(args[i])).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeValue(Object value) {
        if (value == null) {
            return "";
        }
        String valString = String.valueOf(value);
        return valString.replace("\"", "\"\"");
    }
}

/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2013 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package de.micromata.merlin.csv;

import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class for writing comma separated values.
 *
 * @author K.Reinhard@micromata.com
 * @author H.Spiewok@micromata.com (07/2005)
 */
public class CSVWriter {

    protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public final static char DEFAULT_CSV_SEPARATOR_CHAR = ';';

    private final static String DEFAULT_CR = "\n";

    private final PrintWriter out;

    private char csvSeparatorChar = DEFAULT_CSV_SEPARATOR_CHAR;

    private String cr = DEFAULT_CR;

    /**
     * First entry of line?
     */
    private boolean firstEntry = true;

    public CSVWriter(final Writer writer) {
        out = new PrintWriter(writer);
    }

    public void flush() {
        out.flush();
    }

    public CSVWriter writeLine(final Object[] values) {
        for (final Object value : values) {
            write(value);
        }
        writeEndOfLine();
        return this;
    }

    public CSVWriter writeEndOfLine() {
        out.print(cr);
        firstEntry = true;
        return this;
    }

    /**
     * Appends the given value to the buffer.
     *
     * @param value The value to append.
     * @return this for chaining.
     */
    public CSVWriter write(final long value) {
        writeSeparator();
        out.print(value);
        return this;
    }

    /**
     * Appends the given value in the format "yyyy-MM-dd HH:mm:ss.SSS".
     *
     * @param value The value to append.
     * @return this for chaining.
     */
    public CSVWriter write(final Date value) {
        writeSeparator();
        if (value != null) {
            out.print('"');
            out.print(dateFormat.format(value));
            out.print('"');
        }
        return this;
    }

    /**
     * Appends the given value. The string will be encapsulated in quotation marks: " Any occurance of the quotation mark will be quoted by
     * duplication. Example: hallo -&gt; "hallo", hal"lo -&gt; "hal""lo"
     *
     * @param s The value to append.
     * @return this for chaining.
     */
    public CSVWriter write(final String s) {
        writeSeparator();
        if (s != null) {
            out.print('"');
            final int len = s.length();
            char c;
            char[] charArray = s.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                c = charArray[i];
                c = s.charAt(i);
                if ('"' == c) {
                    out.print('"');
                }
                out.print(c);
            }
            out.print('"');
        }
        return this;
    }

    /**
     * Appends the given value to the buffer in the format "yyyy-MM-dd HH:mm:ss.SSS".
     *
     * @param value The value to append.
     * @return this for chaining.
     */
    public CSVWriter write(final Object value) {
        writeSeparator();
        if (value != null) {
            out.print(String.valueOf(value));
        }
        return this;
    }

    /**
     * @param dateFormat The dateFormat to set.
     */
    public void setDateFormat(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setCsvSeparator(final char csvSeparator) {
        this.csvSeparatorChar = csvSeparator;
    }

    public void setCr(final String cr) {
        this.cr = cr;
    }

    private void writeSeparator() {
        if (firstEntry == true)
            firstEntry = false;
        else out.print(csvSeparatorChar);
    }
}

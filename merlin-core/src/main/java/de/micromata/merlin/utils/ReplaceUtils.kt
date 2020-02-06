package de.micromata.merlin.utils

import de.micromata.merlin.word.templating.Variables
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.regex.Pattern

object ReplaceUtils {
    const val IDENTIFIER_REGEXP = "[a-zA-Z_][a-zA-Z\\d_]*"
    @JvmField
    val VARIABLE_PATTERN = Pattern.compile("\\$\\{\\s*($IDENTIFIER_REGEXP)\\s*}")
    val COMMENT_PATTERN = Pattern.compile("\\{\\*[^}]*}")
    const val ALLOWED_FILENAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._-"
    const val PRESERVED_FILENAME_CHARS = "\"*/:<>?\\|"
    const val FILENAME_REPLACE_CHAR = '_'

    private val umlautReplacementMap = mutableMapOf<Char, String>()

    init {
        umlautReplacementMap['Ä'] = "Ae"
        umlautReplacementMap['Ö'] = "Oe"
        umlautReplacementMap['Ü'] = "Ue"
        umlautReplacementMap['ä'] = "ae"
        umlautReplacementMap['ö'] = "oe"
        umlautReplacementMap['ü'] = "ue"
        umlautReplacementMap['ß'] = "ss"

    }

    fun createReplaceEntries(text: String, variables: Variables): List<ReplaceEntry> {
        val replaceEntries: MutableList<ReplaceEntry> = ArrayList()
        createReplaceEntries(text, replaceEntries, variables)
        return replaceEntries
    }

    /**
     * Creates replace entries for variables and comments.
     *
     * @param text           The text to process.
     * @param replaceEntries The replace entries.
     * @param variables      The variables to use.
     */
    @JvmStatic
    fun createReplaceEntries(text: String, replaceEntries: MutableList<ReplaceEntry>, variables: Variables) {
        var matcher = VARIABLE_PATTERN.matcher(text)
        while (matcher.find()) {
            val variableName = matcher.group(1)
            if (!variables.contains(variableName)) {
                continue  // Variable not found. Ignore this finding.
            }
            val value = variables.getFormatted(variableName)
            val start = matcher.start()
            val end = matcher.end()
            replaceEntries.add(ReplaceEntry(start, end, value))
        }
        matcher = COMMENT_PATTERN.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            replaceEntries.add(ReplaceEntry(start, end, ""))
        }
        Collections.sort(replaceEntries, Collections.reverseOrder())
    }

    /**
     * Replaces all occurrences.
     *
     * @param text           The text to process.
     * @param replaceEntries The replace entries.
     * @return The processed text.
     */
    fun replace(text: String, replaceEntries: List<ReplaceEntry>): String {
        var str = text
        for (entry in replaceEntries) {
            val sb = StringBuilder()
            sb.append(str.substring(0, entry.start)).append(entry.newText)
            if (entry.end < str.length) { // Append the tail after ${var}:
                sb.append(str.substring(entry.end))
            }
            str = sb.toString()
        }
        return str
    }

    @JvmStatic
    fun replace(text: String, variables: Variables): String {
        val replaceEntries = createReplaceEntries(text, variables)
        return replace(text, replaceEntries)
    }

    /**
     * Preserved characters (Windows): 0x00-0x1F 0x7F " * / : &lt; &gt; ? \ |
     * Preserved characters (Mac OS): ':'
     * Preserved characters (Unix): '/'
     * Max length: 255
     *
     * @param filename         The filename to encode.
     * @param reducedCharsOnly if true, only [.ALLOWED_FILENAME_CHARS] are allowed and German Umlaute are replaced
     * 'Ä'-&gt;'Ae' etc. If not, all characters excluding [.PRESERVED_FILENAME_CHARS] are allowed and
     * all white spaces will be replaced by ' ' char. Default is true.
     * @return The encoded filename.
     */
    @JvmStatic
    @JvmOverloads
    fun encodeFilename(filename: String?, reducedCharsOnly: Boolean = true): String {
        var modifiedFilename = filename
        if (StringUtils.isEmpty(modifiedFilename)) {
            return "file"
        }
        if (reducedCharsOnly) {
            modifiedFilename = replaceGermanUmlauteAndAccents(modifiedFilename)
        }
        val sb = StringBuilder()
        val charArray = modifiedFilename!!.toCharArray()
        for (i in charArray.indices) {
            val ch = charArray[i]
            if (reducedCharsOnly) {
                if (ALLOWED_FILENAME_CHARS.indexOf(ch) >= 0) {
                    sb.append(ch)
                } else {
                    sb.append(FILENAME_REPLACE_CHAR)
                }
            } else {
                if (ch.toInt() <= 31 || ch.toInt() == 127) { // Not 0x00-0x1F and not 0x7F
                    sb.append(FILENAME_REPLACE_CHAR)
                } else if (PRESERVED_FILENAME_CHARS.indexOf(ch) >= 0) {
                    sb.append(FILENAME_REPLACE_CHAR)
                } else if (Character.isWhitespace(ch)) {
                    sb.append(' ')
                } else {
                    sb.append(ch)
                }
            }
        }
        val result = sb.toString()
        return if (result.length > 255) {
            result.substring(0, 255)
        } else result
    }

    @JvmStatic
    fun replaceGermanUmlauteAndAccents(text: String?): String? {
        if (text.isNullOrBlank()) {
            return text
        }
        val sb = StringBuilder()
        val charArray = text.toCharArray()
        for (i in charArray.indices) {
            val ch = charArray[i]
            if (umlautReplacementMap.containsKey(ch)) {
                sb.append(umlautReplacementMap[ch])
            } else {
                sb.append(ch)
            }
        }
        return StringUtils.stripAccents(sb.toString())
    }
}

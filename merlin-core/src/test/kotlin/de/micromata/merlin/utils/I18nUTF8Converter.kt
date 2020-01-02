package de.micromata.merlin.utils

import org.apache.commons.io.FileUtils
import java.nio.charset.StandardCharsets
import java.nio.file.Path

fun main(args: Array<String>) {
    val path = Path.of("merlin-core", "tmp", "test.properties")//"merlin-core", "src", "main", "resources", "MerlinCoreMessagesBundle_de.properties")
    val file = path.toFile()
    println("Processing '${file.absoluteFile}'...")
    val content = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
    FileUtils.writeByteArrayToFile(file, content.toByteArray(StandardCharsets.ISO_8859_1))
    println("Hello World!")
}

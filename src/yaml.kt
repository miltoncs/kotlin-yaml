package milton.kotlin.yaml

import java.io.File
import java.nio.file.Files

class ParseYaml {
    constructor(contentFile: File) : this(Files.readAllLines(contentFile.toPath()).joinToString(separator = "\n"))
    constructor(contents: String) {
        this.contents = contents.lines().asSequence()
                .filter { !it.contains(Regex("^[\\s]*#.*$")) }
                .filter { !it.matches(Regex("^[\\s]*$")) }
                .joinToString(separator = "\n")

        if (contents.contains(Regex("^---"))) {
            throw Exception("Cannot parse multi-document file, use 'ParseYamlDocuments'.")
        }
    }

    private val contents: String

    operator fun get(key: String): ParseYaml {
        val matchingLines = contents.lines().filter { it.startsWith("$key:") }

        when {
            matchingLines.isEmpty() -> throw Exception("Didn't find key in contents.")
            matchingLines.size > 1 -> throw Exception("Found multiple matching keys.")
        }

        return if (matchingLines.first().matches(Regex("^\\S+:[\\s\\S]+$"))) {
            ParseYaml(matchingLines.first().substringAfter(":").trim())
        } else {
            ParseYaml(findChildContent(key))
        }
    }

    private fun findChildContent(key: String): String {

        val subcontent: MutableList<String> = mutableListOf()
        var passedKey = false
        var passedNextKey = false
        for (line in contents.lines()) {
            if (line.startsWith("$key:")) {
                passedKey = true
            } else if (line.contains(Regex("^[\\S]+:")) && passedKey) {
                passedNextKey = false
            } else if (line.contains(Regex("^[- ]{2}")) && passedKey && !passedNextKey) {
                subcontent.add(line)
            }
        }
        return subcontent.joinToString(separator = "\n").trimIndent()
    }

    fun asList(): List<String> {
        val allLines = contents.trimIndent().lines()

        val elements: MutableList<String> = mutableListOf()
        var currentElement: MutableList<String> = mutableListOf(allLines.first().substringAfter("-").trim())

        for (line in allLines.drop(1)) {
            if (line.startsWith("-")) {
                elements.add(currentElement.joinToString(separator = "\n"))
                currentElement = mutableListOf(line.substringAfter("-").trim())
            } else {
                currentElement.add(line.trim())
            }
        }
        elements.add(currentElement.joinToString(separator = "\n"))

        return elements
    }

    operator fun get(i: Int): ParseYaml = ParseYaml(asList()[i])

    override fun toString(): String = contents
}
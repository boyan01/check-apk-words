package tech.soit.words.lib.scanner.impl

import tech.soit.words.lib.scanner.*
import java.io.File

class PlainFileScanner(workDirectory: File) : FileScanner(workDirectory) {

    override fun accept(file: File): Boolean {
        return file.extension in listOf("txt", "json", "xml")
    }

    override fun scan(file: File, options: ScanOptions): List<ScannerResult> {
        return try {
            val result = mutableListOf<ScannerResult>()
            val readText = file.readText()
            for (word in options.words) {
                if (readText.contains(word, ignoreCase = options.ignoreCase)) {
                    result.add(
                        PlainTextFileResult(
                            path = file.relativePath,
                            keyword = word
                        )
                    )
                }
            }
            result
        } catch (e: Throwable) {
            listOf(ErrorResult(file.relativePath))
        }
    }
}
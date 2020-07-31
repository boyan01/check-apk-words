package tech.soit.words.lib.scanner

import java.io.File

abstract class FileScanner(
    protected val workDirectory: File
) {

    protected val File.relativePath: String get() = relativeTo(workDirectory).path

    abstract fun accept(file: File): Boolean

    abstract fun scan(file: File, options: ScanOptions): List<ScannerResult>

}
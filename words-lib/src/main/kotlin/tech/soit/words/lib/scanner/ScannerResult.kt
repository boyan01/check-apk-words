package tech.soit.words.lib.scanner

import java.io.File


sealed class ScannerResult {
    companion object {
        const val UNKNOWN_ARTIFACT = "unknown"
    }

    open val artifactId: String get() = UNKNOWN_ARTIFACT
    abstract val path: String
}

data class ErrorResult(override val path: String) : ScannerResult() {
    override val artifactId: String
        get() = UNKNOWN_ARTIFACT

    override fun toString(): String {
        return "[error] $path"
    }
}

data class SmaliScannerResult(
    val message: String,
    override val path: String,
    val packageName: String,
    val keyword: String,
    override val artifactId: String = UNKNOWN_ARTIFACT
) : ScannerResult() {
    override fun toString(): String {
        return "[smali] $packageName $message"
    }
}


data class PlainTextFileResult(
    override val artifactId: String = UNKNOWN_ARTIFACT,
    override val path: String,
    val keyword: String
) : ScannerResult() {

    override fun toString(): String {
        return "[${File(path).extension}] $path {$keyword} "
    }
}
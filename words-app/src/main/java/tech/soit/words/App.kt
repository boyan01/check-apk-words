package tech.soit.words

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import tech.soit.words.lib.scanner.ScanOptions
import tech.soit.words.lib.scanner.SensitiveWordsDetector
import tech.soit.words.lib.scanner.SmaliScannerResult
import tech.soit.words.lib.utils.buildPackageNameMapping
import tech.soit.words.lib.utils.deCompileApk
import tech.soit.words.lib.utils.mapResult

fun runApp(
    apk: String,
    mapping: String?,
    words: List<String>,
    ignoreCase: Boolean
) {
    println("apk = $apk")
    println("mapping = $mapping")

    val tempDir = createTempDir("words")
    println("decompiling apk... $tempDir")
    val decompileDir = deCompileApk(apk, tempDir)
    val packageNameDictionary = buildPackageNameMapping(mapping)

    val result = SensitiveWordsDetector.detector(
        decompileDir,
        ScanOptions(words = words, ignoreCase = ignoreCase)
    ).mapResult<SmaliScannerResult> {
        it.copy(
            packageName = packageNameDictionary[it.packageName] ?: it.packageName
        )
    }

    runBlocking {
        val toList = result.toList()
        for (item in toList) {
            println(item)
        }
    }


}
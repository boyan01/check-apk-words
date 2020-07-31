package tech.soit.words.lib.scanner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import tech.soit.words.lib.scanner.impl.PlainFileScanner
import tech.soit.words.lib.scanner.impl.SmaliFileScanner
import java.io.File

object SensitiveWordsDetector {

    private fun createScanners(directory: File): List<FileScanner> {
        return listOf(
            SmaliFileScanner(directory),
            PlainFileScanner(directory)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun detector(directory: File, options: ScanOptions): Flow<ScannerResult> {
        return channelFlow {
            if (!directory.exists()) {
                return@channelFlow
            }
            val scanners = createScanners(directory)
            directory.walkBottomUp().filter { !it.isDirectory }
                .forEach { file ->
                    for (scanner in scanners) {
                        if (!scanner.accept(file)) {
                            continue
                        }
                        launch(Dispatchers.IO) {
                            val scan = scanner.scan(file, options)
                            scan.forEach { send(it) }
                        }
                    }
                }
        }


    }

}
package tech.soit.words.lib.utils

import tech.soit.words.lib.scanner.ScannerResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <reified T : ScannerResult> Flow<ScannerResult>.mapResult(
    crossinline mapper: suspend (T) -> T
): Flow<ScannerResult> {
    return map {
        if (it is T) {
            mapper(it)
        } else {
            it
        }
    }
}


fun StringBuilder.withIndent(indent: Int, builder: StringBuilder.() -> Unit) {
    val text = StringBuilder().apply(builder).toString()
    var indentStr = ""
    for (i in 0 until indent) {
        indentStr += ' '
    }
    append(text.prependIndent(indentStr))
}
package tech.soit.words.lib.scanner.impl


import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTree
import org.jf.smali.smaliFlexLexer
import org.jf.smali.smaliParser
import tech.soit.words.lib.scanner.FileScanner
import tech.soit.words.lib.scanner.ScanOptions
import tech.soit.words.lib.scanner.ScannerResult
import tech.soit.words.lib.scanner.SmaliScannerResult
import tech.soit.words.lib.smali.SmaliElementTypes
import tech.soit.words.lib.utils.childSequence
import tech.soit.words.lib.utils.elementType
import tech.soit.words.lib.utils.text
import java.io.File

class SmaliFileScanner(workDirectory: File) : FileScanner(workDirectory) {

    override fun accept(file: File): Boolean {
        return "smali".equals(file.extension, true)
    }

    override fun scan(file: File, options: ScanOptions): List<ScannerResult> {
        val tree = file.parseToAst()
        val fields = tree.childSequence().map { it as CommonTree }
            .map { it.elementType to it }.toMap()

        val packageName = fields.getValue(SmaliElementTypes.CLASS_DESCRIPTOR).token.text

        val result = mutableListOf<ScannerResult>()
        for ((elementType, subTree) in fields) {
            val treeText = subTree.text()
            options.words.forEach { keyword ->
                if (treeText.contains(keyword, ignoreCase = options.ignoreCase)) {
                    result.add(
                        SmaliScannerResult(
                            packageName = packageName.toJavaPackageName(),
                            path = file.relativePath,
                            message = "$elementType 含有关键字：$keyword",
                            keyword = keyword
                        )
                    )
                }
            }
        }

        return result
    }

    private fun String.toJavaPackageName(): String {
        if (startsWith("L") && endsWith(";")) {
            return removeSurrounding("L", ";").replace('/', '.')
        }
        return this
    }

    private fun File.parseToAst(): CommonTree {
        val reader = readText().reader()
        val lexer = smaliFlexLexer(reader, 19)
        lexer.setSourceFile(this)
        val tokens = CommonTokenStream(lexer)
        val parser = smaliParser(tokens)
        parser.setVerboseErrors(true)
        parser.setApiLevel(19)
        val smaliFile = parser.smali_file()
        return smaliFile.tree
    }

}
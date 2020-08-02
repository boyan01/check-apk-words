package tech.soit.words

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType


fun main(args: Array<String>) {
    val argParser = ArgParser("CheckWords")
    val mapping by argParser.option(
        type = ArgType.String,
        fullName = "mapping",
        shortName = "m",
        description = "mapping file path"
    )
    val apk by argParser.argument(
        type = ArgType.String,
        description = "apk path"
    )
    val words by argParser.argument(
        type = WordsArg,
        description = "senstive words",
        fullName = "words"
    )
    val ignoreCase by argParser.option(
        type = ArgType.Boolean,
        description = "ignore case when check",
        fullName = "ignoreCase"
    )
    argParser.parse(args)
    runApp(apk, mapping, words, ignoreCase ?: false)
}

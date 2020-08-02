package tech.soit.words

import kotlinx.cli.ArgType

object WordsArg : ArgType<List<String>>(true) {
    override val description: kotlin.String
        get() = "{ String, String }"

    override fun convert(value: kotlin.String, name: kotlin.String): List<kotlin.String> {
        return value.split(",").map { it.trim() }
    }
}
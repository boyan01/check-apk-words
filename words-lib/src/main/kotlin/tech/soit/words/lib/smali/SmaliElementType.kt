package tech.soit.words.lib.smali

import org.antlr.runtime.Token
import org.jf.smali.smaliParser

data class SmaliElementType(val name: String, val type: Int) {
    constructor(token: Token) : this(smaliParser.tokenNames[token.type], token.type)
}
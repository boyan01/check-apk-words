package tech.soit.words.lib.utils

import tech.soit.words.lib.smali.SmaliElementType
import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.Tree

fun Tree.childSequence(): Sequence<Tree> {
    return sequence {
        for (i in 0 until childCount) {
            yield(getChild(i))
        }
    }
}

fun Tree.nextSliding(): Tree? {
    val children = parent.childSequence().toList()
    val index = children.indexOf(this)
    if (index == -1) {
        throw IllegalStateException("parent do not contain self? $this")
    }
    return children.getOrNull(index + 1)
}

fun Tree.preSliding(): Tree? {
    val children = parent.childSequence().toList()
    val index = children.indexOf(this)
    if (index == -1) {
        throw IllegalStateException("parent do not contain self? $this")
    }
    return children.getOrNull(index - 1)
}


val CommonTree.elementType: SmaliElementType get() = SmaliElementType(token)


fun Tree.text(): String {
    return if (childCount > 0) {
        val builder = StringBuilder()

        if (!isNil)
            builder.append("(${this} ")

        builder.append(childSequence().joinToString(" ", postfix = " ") {
            it.text()
        })

        if (!isNil)
            builder.append(")")

        builder.toString()
    } else {
        toString()
    }
}
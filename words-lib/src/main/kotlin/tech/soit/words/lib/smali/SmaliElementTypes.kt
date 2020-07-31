package tech.soit.words.lib.smali

import org.jf.smali.smaliParser
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object SmaliElementTypes {

    @Suppress("NOTHING_TO_INLINE")
    private inline fun token(): ReadOnlyProperty<SmaliElementTypes, SmaliElementType> {
        return object : ReadOnlyProperty<SmaliElementTypes, SmaliElementType> {
            override fun getValue(
                thisRef: SmaliElementTypes,
                property: KProperty<*>
            ): SmaliElementType {
                val name = property.name
                return SmaliElementType(name, smaliParser.tokenNames.indexOf(name))
            }
        }
    }

    // 类说明 .class
    val CLASS_DESCRIPTOR by token()

    val I_ACCESS_LIST by token()

    val I_SUPPER by token()

    val I_SOURCE by token()

    val I_METHODS by token()

    val I_FIELDS by token()

    val I_ANNOTATIONS by token()

    val I_IMPLEMENTS by token()

}
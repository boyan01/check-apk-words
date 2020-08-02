package tech.soit.words.lib.utils

import brut.androlib.ApkDecoder
import java.io.File

fun deCompileApk(apkPath: String, parentDir: File): File {
    val temp = File(parentDir, "tmp/de_compile")
    if (temp.exists()) {
        temp.deleteRecursively()
    }
    val apkFile = File(apkPath)

    if (!apkFile.exists()) {
        throw IllegalStateException("can not find $apkPath")
    }

    val decoder = ApkDecoder(apkFile)
    decoder.setOutDir(temp)
    decoder.setForceDelete(true)
    try {
        decoder.decode()
    } finally {
        decoder.close()
    }
    return temp
}


fun buildPackageNameMapping(mappingPath: String?): Map<String, String> {
    mappingPath ?: return emptyMap()
    val lines = File(mappingPath).useLines { sequence ->
        sequence
            .filter { line -> !line.startsWith("#") && !line.startsWith(" ") }
            .toList()
    }
    return lines.map {
        it.removeSuffix(":")
    }.map {
        it.split(" -> ")
    }.map {
        it.last() to it.first()
    }.toMap()
}
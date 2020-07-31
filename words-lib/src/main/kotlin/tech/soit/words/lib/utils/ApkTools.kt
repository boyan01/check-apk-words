package tech.soit.words.lib.utils

import brut.androlib.ApkDecoder
import java.io.File

fun deCompileApk(apkPath: String, buildDir: File): File {
    val temp = File(buildDir, "temp/de_compile")
    if (temp.exists()) {
        temp.deleteRecursively()
    }
    val decoder = ApkDecoder(File(apkPath))
    decoder.setOutDir(temp)
    decoder.setForceDelete(true)
    try {
        decoder.decode()
    } finally {
        decoder.close()
    }
    return temp
}
rootProject.name = "check-apk-words"

include("words-lib")
include("words-gradle-plugin")

val apkToolProjectPath = "./thirdparty/Apktool"
if (!File(apkToolProjectPath).exists()) {
    throw IllegalStateException("""
        
        file `./thirdparty/Apktool` do not exists.
        please update your git submodule first.
        
        git submodule update
        
    """.trimIndent())
}
includeBuild(apkToolProjectPath)

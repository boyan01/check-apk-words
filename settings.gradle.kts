rootProject.name = "check-apk-words"

include("words-lib")
include("words-gradle-plugin")
include("words-app")

val apkToolProjectPath = "./thirdparty/Apktool"
if (!File(rootDir, apkToolProjectPath).exists()) {
    throw IllegalStateException(
        """
        
        file `./thirdparty/Apktool` do not exists.
        please update your git submodule first.
        
        git submodule update
        
    """.trimIndent()
    )
}
includeBuild(apkToolProjectPath)

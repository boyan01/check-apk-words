plugins {
    kotlin("jvm")
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":words-lib"))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    compileOnly("com.android.tools.build:gradle:3.6.1")
}


gradlePlugin {
    isAutomatedPublishing = false
    plugins {
        create("check-apk-words") {
            id = "com.xyz.android.plugin.xyz-sensitive-words"
            implementationClass = "com.xyz.android.plugin.sensitive.XyzSensitiveWordsPlugin"
        }
    }
}

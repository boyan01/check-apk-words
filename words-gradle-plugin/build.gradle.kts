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
            id = "techs.soit.plugin.sensitive-words"
            implementationClass = "tech.soit.words.plugin.SensitiveWordsPlugin"
        }
    }
}

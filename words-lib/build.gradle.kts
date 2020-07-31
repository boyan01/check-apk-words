plugins {
    java
    kotlin("jvm")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
    api(kotlin("stdlib"))

    implementation("Apktool.brut.apktool:apktool-lib")
}

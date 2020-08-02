plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    maven("https://kotlin.bintray.com/kotlinx")
}

application {
    mainClassName = "tech.soit.words.MainKt"
}


dependencies {
    implementation(project(":words-lib"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.2.1")
}
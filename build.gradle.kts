import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
    }
}

subprojects {
    tasks.withType<KotlinCompile>().all {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
        targetCompatibility = "1.8"
        sourceCompatibility = "1.8"
    }
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven("https://plugins.gradle.org/m2/")
    }
    group = "tech.soit.words"
    version = "1.0.0"
}


package tech.soit.words.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl
import tech.soit.words.plugin.tasks.SensitiveWordsScanTask
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
open class SensitiveWordsPlugin : Plugin<Project> {

    companion object {
        private const val CONFIG_SENSITIVE_WORDS = "sensitiveWords"
    }

    override fun apply(project: Project) {
        project.extensions.create(
            CONFIG_SENSITIVE_WORDS,
            SensitiveWordsExtension::class.java
        )
        val android = project.extensions.getByType(AppExtension::class.java)
        android.applicationVariants.all { variant ->
            variant as ApplicationVariantImpl
            val taskFactory = TaskFactoryImpl(project.tasks)
            taskFactory.register(
                SensitiveWordsScanTask.CreationAction(
                    variant = variant,
                    scope = variant.variantData.scope
                )
            )
        }
    }

}
package tech.soit.words.plugin.tasks

import com.android.SdkConstants
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.tasks.NonIncrementalTask
import com.android.build.gradle.internal.tasks.factory.VariantTaskCreationAction
import com.android.builder.core.VariantType
import com.android.builder.dexing.ClassFileInput
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.OutputDirectory
import org.gradle.kotlin.dsl.findByType
import tech.soit.words.lib.scanner.*
import tech.soit.words.lib.utils.buildPackageNameMapping
import tech.soit.words.lib.utils.deCompileApk
import tech.soit.words.lib.utils.mapResult
import tech.soit.words.lib.utils.withIndent
import tech.soit.words.plugin.SensitiveWordsExtension
import java.io.File
import java.util.zip.ZipFile
import kotlin.streams.toList


@Suppress("UnstableApiUsage")
abstract class SensitiveWordsScanTask : NonIncrementalTask() {

    private lateinit var classesArtifacts: ArtifactCollection

    private lateinit var resourceArtifacts: ArtifactCollection

    private lateinit var mappingFile: Provider<FileCollection>

    private lateinit var apkDirectory: Provider<Directory>

    private lateinit var apkFiles: List<File>

    @OutputDirectory
    lateinit var outputDirectory: File

    private fun findApkFile(): File {
        var apkFile = apkDirectory.orNull?.asFile?.listFiles()
            ?.firstOrNull { it.extension == SdkConstants.EXT_ANDROID_PACKAGE }
        if (apkFile != null) return apkFile
        apkFile = apkFiles.firstOrNull()
        if (apkFile != null) return apkFile
        error("无法找到 apk 文件：apkDirectory = ${apkDirectory.orNull}, apkOutputFiles = $apkFiles")
    }

    override fun doTaskAction() {
        val apkFile = findApkFile()
        val mappingFile = mappingFile.orNull?.singleOrNull()
        println("apkPath = $apkFile")
        println("mappingFile = $mappingFile")
        runBlocking {
            run(apkFile.path, mappingFile?.path, project.sensitiveWordsConfig())
        }
    }

    private suspend fun run(
        apkPath: String,
        mappingPath: String?,
        options: SensitiveWordsExtension
    ) {
        println(" 正在反编译 apk... ")
        val output = deCompileApk(apkPath, project.buildDir)

        println(" 正在检测关键字... ")
        val results = SensitiveWordsDetector.detector(output, ScanOptions(options.words, options.ignoreCase))

        // 读取 mapping 文件
        val reMapping = buildPackageNameMapping(mappingPath)

        println(" 正在分析依赖 ")
        val artifactMap = extractArtifactClassesMap(classesArtifacts)
        val resourceMap = extractResourceMap(resourceArtifacts)

        println("收集异常: ${resourceMap.size}")

        val collected = results
            .mapResult<SmaliScannerResult> {
                it.copy(
                    packageName = reMapping[it.packageName] ?: it.packageName,
                    artifactId = artifactMap[it.packageName] ?: it.artifactId
                )
            }.mapResult<PlainTextFileResult> {
                if (it.path.startsWith("res")) {
                    it.copy(artifactId = resourceMap[it.path.removePrefix("res/")] ?: it.artifactId)
                } else {
                    it
                }
            }.toList()
        generateOutput(collected, options)
    }


    private fun generateOutput(
        results: List<ScannerResult>,
        options: SensitiveWordsExtension
    ) {
        outputDirectory.deleteRecursively()
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            error("can not create directory: $outputDirectory")
        }
        val resultsGroupByArtifact = results.groupBy { it.artifactId }
        val file = File(outputDirectory, "artifacts.txt")
        file.createNewFile()
        file.writeText(buildString {
            appendln("# 根据反编译后的资源定位到的含有关键字的依赖:")
            appendln("# 关键词：${options.words.joinToString()}")
            appendln()

            for (artifact in resultsGroupByArtifact.keys) {
                appendln(artifact)
                withIndent(2) {
                    resultsGroupByArtifact[artifact]?.forEach {
                        appendln(it.toString())
                    }
                }
                appendln()
            }
        })
    }


    private fun extractArtifactClassesMap(classesArtifacts: ArtifactCollection): Map<String, String> {
        val map = mutableMapOf<String, String>()
        classesArtifacts.artifacts.forEach { artifact ->
            for (clazz in extractClasses(artifact.file)) {
                map[clazz] = artifact.id.componentIdentifier.displayName
            }
        }
        return map
    }

    private fun extractResourceMap(artifacts: ArtifactCollection): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (artifact in artifacts.artifacts) {
            val artifactName = artifact.id.componentIdentifier.displayName
            val dir = artifact.file
            val paths = dir.walkTopDown()
                .filter { !it.isDirectory }
                .map { it.relativeTo(dir).path }
            for (path in paths) {
                map[path] = artifactName
            }
        }
        return map
    }

    private fun extractClasses(jarFile: File): List<String> = ZipFile(jarFile).use { jar ->
        return jar.stream()
            .filter { ClassFileInput.CLASS_MATCHER.test(it.name) }
            .map { it.name.replace('/', '.').dropLast(SdkConstants.DOT_CLASS.length) }
            .toList()
    }


    class CreationAction(
        private val variant: ApkVariant,
        scope: VariantScope
    ) : VariantTaskCreationAction<SensitiveWordsScanTask>(scope) {

        private val variantType: VariantType = variantScope.variantData.type

        override val name: String
            get() = variantScope.getTaskName("scan", "SensitiveWords")

        override val type: Class<SensitiveWordsScanTask>
            get() = SensitiveWordsScanTask::class.java

        override fun configure(task: SensitiveWordsScanTask) {
            super.configure(task)
            // 只在 APK 构建时启用 task.
            val enable = variantType.isApk
            task.enabled = enable

            task.dependsOn(variantScope.taskContainer.assembleTask)
            task.description = (if (enable) "" else "[已禁用]") +
                    "扫描 ${variantScope.variantData.name} 是否包含词语:" +
                    task.project.sensitiveWordsConfig().words.joinToString()
            task.classesArtifacts = variantScope.getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.CLASSES
            )
            task.resourceArtifacts = variantScope.getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                AndroidArtifacts.ArtifactScope.ALL,
                AndroidArtifacts.ArtifactType.ANDROID_RES
            )
            task.mappingFile = variant.mappingFileProvider
            task.apkDirectory = variantScope.artifacts
                .getFinalProduct(InternalArtifactType.APK)
            // 某些应用会改写 variant output file
            task.apkFiles = variant.outputs.map { it.outputFile }
            task.outputDirectory = File(task.project.buildDir, "output/sensitive-words")
        }

    }


    companion object {
        internal fun Project.sensitiveWordsConfig(): SensitiveWordsExtension {
            return extensions.findByType() ?: SensitiveWordsExtension()
        }
    }

}




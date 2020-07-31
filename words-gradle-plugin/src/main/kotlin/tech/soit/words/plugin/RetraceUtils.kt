package tech.soit.words.plugin

import com.android.tools.r8.retrace.Retrace
import com.android.tools.r8.retrace.RetraceCommand
import java.io.File
import java.util.concurrent.CompletableFuture

class RetraceUtils(
    private val mappingPath: String
) {

    companion object {
        private fun retrace(mappingContent: String, list: List<String>): List<String> {
            val result = CompletableFuture<List<String>>()
            Retrace.run(
                RetraceCommand.a()
                    .a(RetraceCommand.ProguardMapProducer {
                        mappingContent
                    })
                    .a(list)
                    .a { r: List<String> ->
                        result.complete(r)
                    }
                    .a()
            )
            return result.get()
        }

    }

    private val mappingContent = File(mappingPath).readText()


    fun retrace(packageName: String): String {
        return retrace(
            mappingContent,
            listOf(packageName)
        ).first()
    }


    fun retrace(packageNames: List<String>): List<String> {
        return retrace(
            mappingContent,
            packageNames
        )
    }

}



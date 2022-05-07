package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPluginExtension
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.BINARY_DIRECTORY
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

open class HugoServer : DefaultTask() {

    companion object {
        const val HUGO_SERVER = "hugoServer"
    }

    @Nested
    lateinit var extension: HugoPluginExtension

    @Input
    @Optional
    @Option(description = "Hostname (and path) to the root, e.g. http://localhost:1313/documentation/")
    var baseURL: String? = null

    @Input
    @Optional
    @Option(description = "Additional arguments for Hugo server command (defaults to \"\")")
    var args: String = ""

    @TaskAction
    fun run() {
        val baseDir = File("${project.projectDir}/${extension.sourceDirectory}")
        baseDir.mkdirs()
        val arguments = listOf("serve") +
                (baseURL?.let { listOf("--baseURL", it) } ?: emptyList()) +
                args.split(' ')
        project.exec {
            workingDir = baseDir
            executable = "${project.buildDir}/$BINARY_DIRECTORY/hugo"
            args = arguments
        }
    }
}

package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.BINARY_DIRECTORY
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Server task")
abstract class HugoServer : DefaultTask() {

    companion object {
        const val HUGO_SERVER = "hugoServer"
    }

    @get:Input
    abstract val sourceDirectory: Property<String>

    @get:Input
    @get:Optional
    @get:Option(
        option = "baseURL",
        description = "Hostname (and path) to the root, e.g. http://localhost:1313/documentation/"
    )
    abstract val baseURL: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "args", description = "Additional arguments for Hugo server command (defaults to \"\")")
    abstract val args: Property<String>

    @get:Inject
    protected abstract val layout: ProjectLayout

    @get:Inject
    protected abstract val process: ExecOperations

    @TaskAction
    fun run() {
        val baseDir = layout.projectDirectory.dir(sourceDirectory.get()).asFile
        baseDir.mkdirs()
        val arguments = listOf("serve") +
                (baseURL.get().takeIf { it.isNotBlank() }?.let { listOf("--baseURL", it) } ?: emptyList()) +
                args.get().split(' ')
        process.exec {
            workingDir = baseDir
            executable = layout.buildDirectory.dir("$BINARY_DIRECTORY/hugo").get().asFile.absolutePath
            args = arguments
        }
    }
}

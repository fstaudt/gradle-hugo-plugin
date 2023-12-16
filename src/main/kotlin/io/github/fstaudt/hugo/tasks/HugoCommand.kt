package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPluginExtension
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.BINARY_DIRECTORY
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "generic task with unpredictable outputs")
abstract class HugoCommand : DefaultTask() {

    companion object {
        const val HUGO_COMMAND = "hugo"
    }

    @Nested
    lateinit var extension: HugoPluginExtension

    @Input
    @Optional
    @Option(description = "Hugo command to execute (defaults to \"new site .\"")
    var command: String = "new site ."

    @get:Inject
    protected abstract val layout: ProjectLayout

    @get:Inject
    protected abstract val process: ExecOperations

    @TaskAction
    fun run() {
        val baseDir = layout.projectDirectory.dir(extension.sourceDirectory).asFile
        baseDir.mkdirs()
        process.exec {
            workingDir = baseDir
            executable = layout.buildDirectory.dir("$BINARY_DIRECTORY/hugo").get().asFile.absolutePath
            args = command.split(" ")
        }
    }
}

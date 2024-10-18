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

@DisableCachingByDefault(because = "generic task with unpredictable outputs")
abstract class HugoCommand : DefaultTask() {

    companion object {
        const val HUGO_COMMAND = "hugo"
    }

    @get:Input
    abstract val sourceDirectory: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "command", description = "Hugo command to execute (defaults to \"new site .\"")
    abstract val command: Property<String>

    @get:Inject
    protected abstract val layout: ProjectLayout

    @get:Inject
    protected abstract val process: ExecOperations

    @TaskAction
    fun run() {
        val baseDir = layout.projectDirectory.dir(sourceDirectory.get()).asFile
        baseDir.mkdirs()
        process.exec {
            workingDir = baseDir
            executable = layout.buildDirectory.dir("$BINARY_DIRECTORY/hugo").get().asFile.absolutePath
            args = command.get().split(" ")
        }
    }
}

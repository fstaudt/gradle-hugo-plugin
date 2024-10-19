package io.github.fstaudt.hugo.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity.RELATIVE
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

@CacheableTask
abstract class HugoBuild : DefaultTask() {

    companion object {
        const val HUGO_BUILD = "hugoBuild"
        const val PUBLISH_DIRECTORY = "hugo/publish"
    }

    @get:Input
    @get:Optional
    @get:Option(option = "args", description = "Additional arguments for Hugo build command (defaults to \"\")")
    abstract val args: Property<String>

    @get:InputDirectory
    @get:PathSensitive(RELATIVE)
    @get:IgnoreEmptyDirectories
    abstract val sourceDirectory: Property<File>

    @get:InputDirectory
    @get:PathSensitive(RELATIVE)
    abstract val hugoBinaryDirectory: DirectoryProperty

    @get:Input
    abstract val publicationPath: Property<String>

    @get:Inject
    protected abstract val process: ExecOperations

    @get:Inject
    protected abstract val layout: ProjectLayout

    @get:OutputDirectory
    abstract val outputDirectory: Property<File>

    @TaskAction
    fun build() {
        outputDirectory.get().deleteRecursively()
        val arguments =
            listOf("-d", "${outputDirectory.get().absolutePath}/${publicationPath.get()}") + args.get().split(' ')
        process.exec {
            workingDir = sourceDirectory.get()
            executable = hugoBinaryDirectory.file("hugo").get().asFile.absolutePath
            args = arguments
        }
    }
}

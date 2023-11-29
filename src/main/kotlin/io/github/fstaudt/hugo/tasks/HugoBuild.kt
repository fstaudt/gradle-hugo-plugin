package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPluginExtension
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.BINARY_DIRECTORY
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Nested
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

    @Nested
    lateinit var extension: HugoPluginExtension

    @Input
    @Optional
    @Option(description = "Additional arguments for Hugo build command (defaults to \"\")")
    var args: String = ""

    @InputDirectory
    @PathSensitive(RELATIVE)
    @IgnoreEmptyDirectories
    lateinit var sourceDirectory: File

    @OutputDirectory
    var outputDirectory: File = layout.buildDirectory.dir(PUBLISH_DIRECTORY).get().asFile

    @Input
    var publicationPath: String = ""

    @get:Inject
    protected abstract val process: ExecOperations

    @get:Inject
    protected abstract val layout: ProjectLayout

    @TaskAction
    fun build() {
        outputDirectory.deleteRecursively()
        val arguments = listOf("-d", "${outputDirectory.absolutePath}/$publicationPath") + args.split(' ')
        process.exec {
            workingDir = sourceDirectory
            executable = layout.buildDirectory.dir("$BINARY_DIRECTORY/hugo").get().asFile.absolutePath
            args = arguments
        }
    }
}

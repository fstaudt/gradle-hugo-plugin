package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPluginExtension
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.BINARY_DIRECTORY
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.work.DisableCachingByDefault
import java.io.File

@DisableCachingByDefault(because = "Unknown inputs")
open class HugoCommand : DefaultTask() {

    companion object {
        const val HUGO_COMMAND = "hugo"
    }

    @Nested
    lateinit var extension: HugoPluginExtension

    @Input
    @Optional
    @Option(description = "Hugo command to execute (defaults to \"new site .\"")
    var command: String = "new site ."

    @TaskAction
    fun run() {
        val baseDir = File("${project.projectDir}/${extension.sourceDirectory}")
        baseDir.mkdirs()
        project.exec {
            workingDir = baseDir
            executable = "${project.buildDir}/$BINARY_DIRECTORY/hugo"
            args = command.split(" ")
        }
    }
}

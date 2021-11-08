package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipException

@CacheableTask
open class HugoDownload : DefaultTask() {

    companion object {
        const val HUGO_DOWNLOAD = "hugoDownload"
        const val DOWNLOAD_DIRECTORY = "hugo/download"
        const val BINARY_DIRECTORY = "hugo/bin"
    }

    @Nested
    lateinit var extension: HugoPluginExtension

    @OutputDirectory
    val output = File("${project.buildDir}/$BINARY_DIRECTORY")

    @TaskAction
    fun download() {
        with(project) {
            downloadArchive().also {
                when (it.extension) {
                    "zip" -> copy { from(zipTree(it)); into(output) }
                    "tgz", "tar", "gz" -> copy { from(tarTree(it)); into(output) }
                    else -> throw ZipException("Unsupported extension for archive ${it.name}.")
                }
            }
        }
    }

    private fun downloadArchive(): File {
        val url = extension.downloadUrl()
        return File("${project.buildDir}/$DOWNLOAD_DIRECTORY/${url.file}").also {
            it.ensureParentDirsCreated()
            logger.info("Downloading Hugo binary from $url")
            url.openStream().use { input ->
                FileOutputStream(it).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}

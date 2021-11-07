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
import java.net.URL
import java.text.MessageFormat
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
            val downloadedArchive = downloadArchive()
            when (downloadedArchive.extension) {
                "zip" -> copy { from(zipTree(downloadedArchive)); into(output) }
                "tgz", "tar", "gz" -> copy { from(tarTree(downloadedArchive)); into(output) }
                else -> throw ZipException("Unsupported extension for archive ${downloadedArchive.name}.")
            }
        }
    }

    private fun downloadArchive(): File {
        val url = URL(MessageFormat.format(extension.downloadUrl(), extension.version))
        return File("${project.buildDir}/$DOWNLOAD_DIRECTORY/${url.file}").also {
            it.ensureParentDirsCreated()
            url.openStream().use { input ->
                FileOutputStream(it).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}

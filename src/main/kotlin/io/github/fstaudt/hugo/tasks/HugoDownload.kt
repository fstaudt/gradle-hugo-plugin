package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipException
import javax.inject.Inject

@CacheableTask
abstract class HugoDownload : DefaultTask() {

    companion object {
        const val HUGO_DOWNLOAD = "hugoDownload"
        const val DOWNLOAD_DIRECTORY = "hugo/download"
        const val BINARY_DIRECTORY = "hugo/bin"
    }

    @Nested
    lateinit var extension: HugoPluginExtension

    @OutputDirectory
    val output = layout.buildDirectory.dir(BINARY_DIRECTORY).get().asFile

    @get:Inject
    protected abstract val layout: ProjectLayout

    @get:Inject
    protected abstract val fs: FileSystemOperations

    @get:Inject
    protected abstract val archives: ArchiveOperations

    @TaskAction
    fun download() {
        downloadArchive().also {
            when (it.extension) {
                "zip" -> fs.copy { from(archives.zipTree(it)); into(output) }
                "tgz", "tar", "gz" -> fs.copy { from(archives.tarTree(it)); into(output) }
                else -> throw ZipException("Unsupported extension for archive ${it.name}.")
            }
        }
    }

    private fun downloadArchive(): File {
        val url = extension.downloadUrl()
        return layout.buildDirectory.file("$DOWNLOAD_DIRECTORY/${url.file}").get().asFile.also {
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

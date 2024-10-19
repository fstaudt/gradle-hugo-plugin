package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.OsFamily
import io.github.fstaudt.hugo.OsFamily.MAC
import io.github.fstaudt.hugo.OsFamily.UNIX
import io.github.fstaudt.hugo.OsFamily.WINDOWS
import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.MessageFormat
import java.util.zip.ZipException
import javax.inject.Inject

@CacheableTask
abstract class HugoDownload : DefaultTask() {

    companion object {
        const val HUGO_DOWNLOAD = "hugoDownload"
        const val DOWNLOAD_DIRECTORY = "hugo/download"
        const val BINARY_DIRECTORY = "hugo/bin"
        const val HUGO_PATH = "gohugoio/hugo/releases/download"
        const val WINDOWS_DOWNLOAD_URL = "https://github.com/$HUGO_PATH/v{0}/hugo_extended_{0}_windows-amd64.zip"
        const val LINUX_DOWNLOAD_URL = "https://github.com/$HUGO_PATH/v{0}/hugo_extended_{0}_linux-amd64.tar.gz"
        const val MAC_OS_DOWNLOAD_URL = "https://github.com/$HUGO_PATH/v{0}/hugo_extended_{0}_darwin-universal.tar.gz"
    }

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val windowsDownloadUrl: Property<String>

    @get:Input
    abstract val linuxDownloadUrl: Property<String>

    @get:Input
    abstract val macOSDownloadUrl: Property<String>

    @get:Input
    abstract val osFamily: Property<OsFamily>

    @get:Inject
    protected abstract val layout: ProjectLayout

    @get:Inject
    protected abstract val fs: FileSystemOperations

    @get:Inject
    protected abstract val archives: ArchiveOperations

    @get:OutputDirectory
    abstract val hugoBinaryDirectory: DirectoryProperty

    @TaskAction
    fun download() {
        downloadArchive().also {
            when (it.extension) {
                "zip" -> fs.copy { from(archives.zipTree(it)); into(hugoBinaryDirectory) }
                "tgz", "tar", "gz" -> fs.copy { from(archives.tarTree(it)); into(hugoBinaryDirectory) }
                else -> throw ZipException("Unsupported extension for archive ${it.name}.")
            }
        }
    }

    private fun downloadArchive(): File {
        val url = downloadUrl()
        return layout.buildDirectory.file("$DOWNLOAD_DIRECTORY/${url.file}").get().asFile.also {
            it.ensureParentDirsCreated()
            if (it.exists()) {
                logger.info("Archive already exists. Skipping download")
            } else {
                logger.info("Downloading Hugo binary from $url")
                url.openStream().use { input ->
                    FileOutputStream(it).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    private fun downloadUrl(): URL {
        return when (osFamily.get()) {
            WINDOWS -> windowsDownloadUrl
            MAC -> macOSDownloadUrl
            UNIX -> linuxDownloadUrl
        }.let { URL(MessageFormat.format(it.get(), version.get())) }
    }
}

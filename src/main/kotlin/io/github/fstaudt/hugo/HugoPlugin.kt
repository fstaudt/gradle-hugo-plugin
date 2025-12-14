package io.github.fstaudt.hugo

import io.github.fstaudt.hugo.OsFamily.MAC
import io.github.fstaudt.hugo.OsFamily.UNIX
import io.github.fstaudt.hugo.OsFamily.WINDOWS
import io.github.fstaudt.hugo.tasks.HugoBuild
import io.github.fstaudt.hugo.tasks.HugoBuild.Companion.HUGO_BUILD
import io.github.fstaudt.hugo.tasks.HugoBuild.Companion.PUBLISH_DIRECTORY
import io.github.fstaudt.hugo.tasks.HugoCommand
import io.github.fstaudt.hugo.tasks.HugoCommand.Companion.HUGO_COMMAND
import io.github.fstaudt.hugo.tasks.HugoDownload
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.BINARY_DIRECTORY
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.HUGO_DOWNLOAD
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.LINUX_DOWNLOAD_URL
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.MAC_OS_DOWNLOAD_URL
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.WINDOWS_DOWNLOAD_URL
import io.github.fstaudt.hugo.tasks.HugoServer
import io.github.fstaudt.hugo.tasks.HugoServer.Companion.HUGO_SERVER
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_UNIX
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS
import org.apache.tools.ant.taskdefs.condition.Os.isFamily
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import java.io.File

class HugoPlugin : Plugin<Project> {

    companion object {
        const val HUGO = "hugo"
        const val HUGO_VERSION = "0.152.2"
        const val SOURCE_DIRECTORY = "site"
        private val LOGGER: Logger = Logging.getLogger(HugoPlugin::class.java)
    }

    override fun apply(project: Project) {
        with(project) {
            val pluginExtension = extensions.create<HugoPluginExtension>(HUGO).apply {
                version.convention(HUGO_VERSION)
                sourceDirectory.convention(SOURCE_DIRECTORY)
                windowsDownloadUrl.convention(WINDOWS_DOWNLOAD_URL)
                linuxDownloadUrl.convention(LINUX_DOWNLOAD_URL)
                macOSDownloadUrl.convention(MAC_OS_DOWNLOAD_URL)
                osFamily.convention(defaultOsFamily())
                environmentVariables.convention(emptyMap())
            }
            val hugoDownload = tasks.register<HugoDownload>(HUGO_DOWNLOAD) {
                group = HUGO
                description = "Download Hugo binary for the current OS (Windows, macOS or Linux)."
                version.set(pluginExtension.version)
                windowsDownloadUrl.set(pluginExtension.windowsDownloadUrl)
                linuxDownloadUrl.set(pluginExtension.linuxDownloadUrl)
                macOSDownloadUrl.set(pluginExtension.macOSDownloadUrl)
                osFamily.set(pluginExtension.osFamily)
                hugoBinaryDirectory.set(layout.buildDirectory.dir(BINARY_DIRECTORY))
            }
            tasks.register<HugoCommand>(HUGO_COMMAND) {
                group = HUGO
                description = "Execute any Hugo command (e.g. new, gen ...)."
                sourceDirectory.set(pluginExtension.sourceDirectory)
                hugoBinaryDirectory.set(hugoDownload.flatMap { it.hugoBinaryDirectory })
                command.convention("new site .")
                environmentVariables.set(pluginExtension.environmentVariables)
            }
            tasks.register<HugoBuild>(HUGO_BUILD) {
                group = HUGO
                description = "Build Hugo static site for publication."
                args.convention("")
                sourceDirectory.set(pluginExtension.sourceDirectory.map { File(projectDir, it) })
                hugoBinaryDirectory.set(hugoDownload.flatMap { it.hugoBinaryDirectory })
                publicationPath.convention("")
                outputDirectory.set(layout.buildDirectory.dir(PUBLISH_DIRECTORY).map { it.asFile })
                environmentVariables.set(pluginExtension.environmentVariables)
            }
            tasks.register<HugoServer>(HUGO_SERVER) {
                group = HUGO
                description = "Run server for development of Hugo static site."
                sourceDirectory.set(pluginExtension.sourceDirectory)
                hugoBinaryDirectory.set(hugoDownload.flatMap { it.hugoBinaryDirectory })
                args.convention("")
                environmentVariables.set(pluginExtension.environmentVariables)
            }
        }
    }

    private fun defaultOsFamily() = when {
        isFamily(FAMILY_WINDOWS) -> WINDOWS
        isFamily(FAMILY_MAC) -> MAC
        isFamily(FAMILY_UNIX) -> UNIX
        else -> UNIX.also { LOGGER.warn("Unable to derive OS family from system properties, falling back on UNIX.") }
    }
}

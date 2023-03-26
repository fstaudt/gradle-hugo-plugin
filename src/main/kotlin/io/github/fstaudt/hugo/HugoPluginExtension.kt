package io.github.fstaudt.hugo

import io.github.fstaudt.hugo.OsFamily.MAC
import io.github.fstaudt.hugo.OsFamily.UNIX
import io.github.fstaudt.hugo.OsFamily.WINDOWS
import org.apache.tools.ant.taskdefs.condition.Os
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_UNIX
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import java.net.URL
import java.text.MessageFormat

open class HugoPluginExtension {
    companion object {
        val LOGGER: Logger = Logging.getLogger(HugoPluginExtension::class.java)
        const val HUGO_VERSION = "0.111.3"
        const val SOURCE_DIRECTORY = "site"
        const val WINDOWS_DOWNLOAD_URL = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_windows-amd64.zip"
        const val LINUX_DOWNLOAD_URL = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_linux-amd64.tar.gz"
        const val MAC_OS_DOWNLOAD_URL = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_darwin-universal.tar.gz"
    }

    @Input
    var version: String = HUGO_VERSION

    @Input
    var sourceDirectory: String = SOURCE_DIRECTORY

    @Input
    var windowsDownloadUrl: String = WINDOWS_DOWNLOAD_URL

    @Input
    var linuxDownloadUrl: String = LINUX_DOWNLOAD_URL

    @Input
    var macOSDownloadUrl: String = MAC_OS_DOWNLOAD_URL

    @Input
    var osFamily: OsFamily = when {
        Os.isFamily(FAMILY_WINDOWS) -> WINDOWS
        Os.isFamily(FAMILY_MAC) -> MAC
        Os.isFamily(FAMILY_UNIX) -> UNIX
        else -> UNIX.also { LOGGER.warn("Unable to derive OS family from system properties, falling back on UNIX.") }
    }

    fun downloadUrl(): URL {
        return when (osFamily) {
            WINDOWS -> windowsDownloadUrl
            MAC -> macOSDownloadUrl
            UNIX -> linuxDownloadUrl
        }.let { URL(MessageFormat.format(it, version)) }
    }
}

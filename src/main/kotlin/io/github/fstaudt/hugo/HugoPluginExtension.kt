package io.github.fstaudt.hugo

import org.apache.tools.ant.taskdefs.condition.Os
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS
import org.gradle.api.tasks.Input

open class HugoPluginExtension {
    companion object {
        const val HUGO_VERSION = "0.89.1"
        const val SOURCE_DIRECTORY = "src/main/hugo"
        const val WINDOWS_DOWNLOAD_URL = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_Windows-64bit.zip"
        const val LINUX_DOWNLOAD_URL = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_Linux-64bit.tar.gz"
        const val MAC_OS_DOWNLOAD_URL = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_macOS-64bit.tar.gz"
    }

    @Input
    var version: String = HUGO_VERSION

    @Input
    var windowsDownloadUrl: String = WINDOWS_DOWNLOAD_URL

    @Input
    var linuxDownloadUrl: String = LINUX_DOWNLOAD_URL

    @Input
    var macOSDownloadUrl: String = MAC_OS_DOWNLOAD_URL

    fun downloadUrl(): String {
        return when (true) {
            Os.isFamily(FAMILY_WINDOWS) -> windowsDownloadUrl
            Os.isFamily(FAMILY_MAC) -> macOSDownloadUrl
            else -> linuxDownloadUrl
        }
    }
}

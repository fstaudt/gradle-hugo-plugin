package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPlugin.Companion.HUGO
import io.github.fstaudt.hugo.HugoPluginExtension.Companion.LINUX_DOWNLOAD_URL
import io.github.fstaudt.hugo.HugoPluginExtension.Companion.MAC_OS_DOWNLOAD_URL
import io.github.fstaudt.hugo.HugoPluginExtension.Companion.WINDOWS_DOWNLOAD_URL
import io.github.fstaudt.hugo.TestProject
import io.github.fstaudt.hugo.WITH_BUILD_CACHE
import io.github.fstaudt.hugo.buildDir
import io.github.fstaudt.hugo.initBuildFile
import io.github.fstaudt.hugo.run
import io.github.fstaudt.hugo.runAndFail
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.BINARY_DIRECTORY
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.DOWNLOAD_DIRECTORY
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.HUGO_DOWNLOAD
import io.github.fstaudt.hugo.testProject
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome.FAILED
import org.gradle.testkit.runner.TaskOutcome.FROM_CACHE
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class HugoDownloadTest {

    private lateinit var testProject: TestProject

    @BeforeEach
    fun `init test project`() {
        testProject = testProject()
    }

    @AfterEach
    fun `cleanup test project`() {
        testProject.deleteRecursively()
    }

    @Test
    fun `hugoDownload should download windows hugo binary and unzip it in build dir`() {
        testProject.initBuildFile {
            appendText("""
                val downloadUrl = "$WINDOWS_DOWNLOAD_URL"
                $HUGO {
                  windowsDownloadUrl = downloadUrl
                  linuxDownloadUrl = downloadUrl
                  macOSDownloadUrl = downloadUrl
                }
            """.trimIndent())
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
        }
    }

    @Test
    fun `hugoDownload should download linux hugo binary and untar it in build dir`() {
        testProject.initBuildFile {
            appendText("""
                val downloadUrl = "$LINUX_DOWNLOAD_URL"
                $HUGO {
                  windowsDownloadUrl = downloadUrl
                  linuxDownloadUrl = downloadUrl
                  macOSDownloadUrl = downloadUrl
                }
            """.trimIndent())
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
    }

    @Test
    fun `hugoDownload should download macOS hugo binary and untar it in build dir`() {
        testProject.initBuildFile {
            appendText("""
                val downloadUrl = "$MAC_OS_DOWNLOAD_URL"
                $HUGO {
                  windowsDownloadUrl = downloadUrl
                  linuxDownloadUrl = downloadUrl
                  macOSDownloadUrl = downloadUrl
                }
            """.trimIndent())
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
    }

    @Test
    fun `hugoDownload should fail when hugo binary can't be downloaded`() {
        testProject.initBuildFile {
            appendText("""
                val invalidUrl = "https://dummy.com/invalid.zip"
                $HUGO {
                  windowsDownloadUrl = invalidUrl
                  linuxDownloadUrl = invalidUrl
                  macOSDownloadUrl = invalidUrl
                }
            """.trimIndent())
        }
        testProject.runAndFail(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(FAILED)
        }
    }

    @Test
    fun `hugoDownload should retrieve binary from cache when it was already executed`() {
        testProject.initBuildFile {
            appendText("""
                val downloadUrl = "$WINDOWS_DOWNLOAD_URL"
                $HUGO {
                  windowsDownloadUrl = downloadUrl
                  linuxDownloadUrl = downloadUrl
                  macOSDownloadUrl = downloadUrl
                }
            """.trimIndent())
        }
        testProject.run(WITH_BUILD_CACHE, HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
        }
        File("${testProject.buildDir}/hugo").deleteRecursively()
        testProject.run(WITH_BUILD_CACHE, HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(FROM_CACHE)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY")).doesNotExist()
        }
    }

    @Test
    fun `hugoDownload should download hugo binary for requested version`() {
        val version = "0.89.0"
        testProject.initBuildFile {
            appendText("""
                val downloadUrl = "$WINDOWS_DOWNLOAD_URL"
                $HUGO {
                  windowsDownloadUrl = downloadUrl
                  linuxDownloadUrl = downloadUrl
                  macOSDownloadUrl = downloadUrl
                  version = "$version"
                }
            """.trimIndent())
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/gohugoio/hugo/releases/download/v$version")).isDirectory
        }
    }
}

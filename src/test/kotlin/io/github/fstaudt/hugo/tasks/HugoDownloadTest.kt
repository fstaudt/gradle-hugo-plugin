package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPlugin.Companion.HUGO
import io.github.fstaudt.hugo.HugoPluginExtensionTest.Companion.LINUX_ARCHIVE
import io.github.fstaudt.hugo.HugoPluginExtensionTest.Companion.MAC_OS_ARCHIVE
import io.github.fstaudt.hugo.HugoPluginExtensionTest.Companion.WINDOWS_ARCHIVE
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
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.apache.tools.ant.taskdefs.condition.Os
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_UNIX
import org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome.FAILED
import org.gradle.testkit.runner.TaskOutcome.FROM_CACHE
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class HugoDownloadTest {

    private lateinit var testProject: TestProject

    @BeforeEach
    fun `init test project`() {
        testProject = testProject()
        mockkStatic(Os::class)
    }

    @AfterEach
    fun `cleanup test project`() {
        testProject.deleteRecursively()
        clearAllMocks()
    }

    @Test
    fun `hugoDownload should download Windows Hugo binary when OS family is Windows`() {
        testProject.initBuildFile {
            appendText("""
                $HUGO {
                  osFamily = io.github.fstaudt.hugo.OsFamily.WINDOWS
                }
            """.trimIndent())
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$WINDOWS_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
        }
    }

    @Test
    fun `hugoDownload should download linux Hugo binary when OS family is Unix`() {
        testProject.initBuildFile {
            appendText("""
                $HUGO {
                  osFamily = io.github.fstaudt.hugo.OsFamily.UNIX
                }
            """.trimIndent())
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$LINUX_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
    }

    @Test
    fun `hugoDownload should download macOS Hugo binary when OS family is macOS`() {
        testProject.initBuildFile {
            appendText("""
                $HUGO {
                  osFamily = io.github.fstaudt.hugo.OsFamily.MAC
                }
            """.trimIndent())
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$MAC_OS_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
    }

    @Test
    @Disabled("mock of static methods is ineffective with gradle test kit")
    fun `hugoDownload should log warning and download linux Hugo binary when OS family can't be derived from system property`() {
        every { Os.isFamily(FAMILY_WINDOWS) }.returns(false)
        every { Os.isFamily(FAMILY_UNIX) }.returns(false)
        every { Os.isFamily(FAMILY_MAC) }.returns(false)
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(it.output).contains("Unable to derive OS family from system properties, falling back on UNIX.")
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$LINUX_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
    }

    @Test
    fun `hugoDownload should fail when download URL is invalid`() {
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
    fun `hugoDownload should retrieve Hugo binary from cache when it was already executed`() {
        testProject.initBuildFile {
            appendText("""
                $HUGO {
                  osFamily = io.github.fstaudt.hugo.OsFamily.WINDOWS
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
    @Disabled("mock of static methods is ineffective with gradle test kit")
    fun `hugoDownload should download Hugo binary when it was already cached for another OS family`() {
        every { Os.isFamily(FAMILY_WINDOWS) }.returns(false)
        every { Os.isFamily(FAMILY_UNIX) }.returns(true)
        testProject.run(WITH_BUILD_CACHE, HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
        every { Os.isFamily(FAMILY_WINDOWS) }.returns(true)
        every { Os.isFamily(FAMILY_UNIX) }.returns(false)
        File("${testProject.buildDir}/hugo").deleteRecursively()
        testProject.run(WITH_BUILD_CACHE, HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
        }
    }

    @Test
    fun `hugoDownload should download Hugo binary for requested version`() {
        val version = "0.93.0"
        testProject.initBuildFile {
            appendText("""
                $HUGO {
                  osFamily = io.github.fstaudt.hugo.OsFamily.WINDOWS
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

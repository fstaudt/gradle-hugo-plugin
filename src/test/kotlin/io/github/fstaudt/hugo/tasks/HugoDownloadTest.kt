package io.github.fstaudt.hugo.tasks

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.http.Body
import io.github.fstaudt.hugo.HugoPlugin.Companion.HUGO
import io.github.fstaudt.hugo.TestProject
import io.github.fstaudt.hugo.WITH_BUILD_CACHE
import io.github.fstaudt.hugo.buildDir
import io.github.fstaudt.hugo.conditions.ForGradleVersion
import io.github.fstaudt.hugo.initBuildFile
import io.github.fstaudt.hugo.run
import io.github.fstaudt.hugo.runAndFail
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.BINARY_DIRECTORY
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.DOWNLOAD_DIRECTORY
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.HUGO_DOWNLOAD
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.HUGO_PATH
import io.github.fstaudt.hugo.testProject
import io.mockk.clearAllMocks
import io.mockk.mockkStatic
import org.apache.tools.ant.taskdefs.condition.Os
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome.FAILED
import org.gradle.testkit.runner.TaskOutcome.FROM_CACHE
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class HugoDownloadTest {
    companion object {
        private const val TEST_HUGO_BINARIES_DIRECTORY = "src/test/resources/hugo-binaries"
        const val WINDOWS_ARCHIVE = "$HUGO_PATH/v0.136.2/hugo_extended_0.136.2_windows-amd64.zip"
        const val LINUX_ARCHIVE = "$HUGO_PATH/v0.136.2/hugo_extended_0.136.2_linux-amd64.tar.gz"
        const val MAC_OS_ARCHIVE = "$HUGO_PATH/v0.136.2/hugo_extended_0.136.2_darwin-universal.tar.gz"

        private lateinit var wiremock: WireMockServer

        @BeforeAll
        @JvmStatic
        fun startWiremock() {
            wiremock = WireMockServer(options().dynamicPort())
            wiremock.start()
            wiremock.stubForHugoBinaries()
        }

        @AfterAll
        @JvmStatic
        fun stopWiremock() {
            wiremock.stop()
        }

        private fun WireMockServer.stubForHugoBinaries() {
            stubForHugo(WINDOWS_ARCHIVE)
            stubForHugo(LINUX_ARCHIVE)
            stubForHugo(MAC_OS_ARCHIVE)
            stubForHugo("$HUGO_PATH/v0.104.2/hugo_extended_0.104.2_windows-amd64.zip")
        }

        private fun WireMockServer.stubForHugo(path: String) {
            val binary = File("$TEST_HUGO_BINARIES_DIRECTORY/$path").readBytes()
            stubFor(get("/$path").willReturn(ok().withResponseBody(Body(binary))))
        }

        private fun setDownloadUrlsForWiremock(): String {
            val port = wiremock.port()
            return """
                windowsDownloadUrl.set("http://localhost:$port/$HUGO_PATH/v{0}/hugo_extended_{0}_windows-amd64.zip")
                linuxDownloadUrl.set("http://localhost:$port/$HUGO_PATH/v{0}/hugo_extended_{0}_linux-amd64.tar.gz")
                macOSDownloadUrl.set("http://localhost:$port/$HUGO_PATH/v{0}/hugo_extended_{0}_darwin-universal.tar.gz")
            """
        }
    }

    private lateinit var testProject: TestProject

    @BeforeEach
    fun `init test project`() {
        testProject = testProject()
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                }
            """.trimIndent()
            )
        }
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
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily.set(io.github.fstaudt.hugo.OsFamily.WINDOWS)
                }
            """.trimIndent()
            )
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$WINDOWS_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
        }
    }

    @Test
    @ForGradleVersion(aboveOrEqualTo = "8.2")
    fun `hugoDownload should download Windows Hugo binary when download URL is set by assignment`() {
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  osFamily = io.github.fstaudt.hugo.OsFamily.WINDOWS
                  windowsDownloadUrl = "http://localhost:${wiremock.port()}/$WINDOWS_ARCHIVE"
                }
            """.trimIndent()
            )
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
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily.set(io.github.fstaudt.hugo.OsFamily.UNIX)
                }
            """.trimIndent()
            )
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$LINUX_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
    }

    @Test
    @ForGradleVersion(aboveOrEqualTo = "8.2")
    fun `hugoDownload should download linux Hugo binary when download URL is set by assignment`() {
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  osFamily = io.github.fstaudt.hugo.OsFamily.UNIX
                  linuxDownloadUrl = "http://localhost:${wiremock.port()}/$LINUX_ARCHIVE"
                }
            """.trimIndent()
            )
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
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily.set(io.github.fstaudt.hugo.OsFamily.MAC)
                }
            """.trimIndent()
            )
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$MAC_OS_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
    }

    @Test
    @ForGradleVersion(aboveOrEqualTo = "8.2")
    fun `hugoDownload should download macOS Hugo binary when download URL is set by assignment`() {
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  osFamily = io.github.fstaudt.hugo.OsFamily.MAC
                  macOSDownloadUrl = "http://localhost:${wiremock.port()}/$MAC_OS_ARCHIVE"
                }
            """.trimIndent()
            )
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$MAC_OS_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo")).isFile
        }
    }

    @Test
    @ForGradleVersion(aboveOrEqualTo = "8.2")
    fun `hugoDownload should download Windows Hugo binary when OS family is set by assignment`() {
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily = io.github.fstaudt.hugo.OsFamily.WINDOWS
                }
            """.trimIndent()
            )
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$WINDOWS_ARCHIVE")).isFile
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
        }
    }

    @Test
    fun `hugoDownload should fail when download URL is invalid`() {
        testProject.initBuildFile {
            appendText(
                """
                val invalidUrl = "https://dummy.com/invalid.zip"
                $HUGO {
                  windowsDownloadUrl.set(invalidUrl)
                  linuxDownloadUrl.set(invalidUrl)
                  macOSDownloadUrl.set(invalidUrl)
                }
            """.trimIndent()
            )
        }
        testProject.runAndFail(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(FAILED)
        }
    }

    @Test
    fun `hugoDownload should retrieve Hugo binary from cache when it was already executed`() {
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily.set(io.github.fstaudt.hugo.OsFamily.WINDOWS)
                }
            """.trimIndent()
            )
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
    fun `hugoDownload should be up-to-date when it was already executed`() {
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily.set(io.github.fstaudt.hugo.OsFamily.WINDOWS)
                }
            """.trimIndent()
            )
        }
        testProject.run(WITH_BUILD_CACHE, HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
        }
        File("${testProject.buildDir}/hugo/download").deleteRecursively()
        testProject.run(WITH_BUILD_CACHE, HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(UP_TO_DATE)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY")).doesNotExist()
        }
    }

    @Test
    fun `hugoDownload should skip download when archive is already downloaded`() {
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily.set(io.github.fstaudt.hugo.OsFamily.WINDOWS)
                }
            """.trimIndent()
            )
        }
        File("$TEST_HUGO_BINARIES_DIRECTORY/$WINDOWS_ARCHIVE")
            .copyTo(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/$WINDOWS_ARCHIVE"))
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
            assertThat(it.output).contains("Archive already exists. Skipping download")
        }
    }

    @Test
    fun `hugoDownload should download Hugo binary for requested version`() {
        val version = "0.104.2"
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily.set(io.github.fstaudt.hugo.OsFamily.WINDOWS)
                  version.set("$version")
                }
            """.trimIndent()
            )
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/gohugoio/hugo/releases/download/v$version")).isDirectory
        }
    }

    @Test
    @ForGradleVersion(aboveOrEqualTo = "8.2")
    fun `hugoDownload should download Hugo binary for requested version set by assignment`() {
        val version = "0.104.2"
        testProject.initBuildFile {
            appendText(
                """
                $HUGO {
                  ${setDownloadUrlsForWiremock()}
                  osFamily = io.github.fstaudt.hugo.OsFamily.WINDOWS
                  version = "$version"
                }
            """.trimIndent()
            )
        }
        testProject.run(HUGO_DOWNLOAD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$BINARY_DIRECTORY/hugo.exe")).isFile
            assertThat(File("${testProject.buildDir}/$DOWNLOAD_DIRECTORY/gohugoio/hugo/releases/download/v$version")).isDirectory
        }
    }
}

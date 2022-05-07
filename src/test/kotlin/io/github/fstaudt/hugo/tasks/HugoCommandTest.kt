package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPluginExtension.Companion.HUGO_VERSION
import io.github.fstaudt.hugo.HugoPluginExtension.Companion.SOURCE_DIRECTORY
import io.github.fstaudt.hugo.TestProject
import io.github.fstaudt.hugo.WITH_BUILD_CACHE
import io.github.fstaudt.hugo.initBuildFile
import io.github.fstaudt.hugo.run
import io.github.fstaudt.hugo.runAndFail
import io.github.fstaudt.hugo.tasks.HugoCommand.Companion.HUGO_COMMAND
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

class HugoCommandTest {

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
    fun `hugo should build new hugo site in base directory by default`() {
        testProject.run(WITH_BUILD_CACHE, HUGO_COMMAND).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(it.task(":$HUGO_COMMAND")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject}/$SOURCE_DIRECTORY/config.toml")).isFile
        }
    }

    @Test
    fun `hugo should run hugo command in requested source directory`() {
        testProject.initBuildFile {
            appendText("""
                hugo {
                  sourceDirectory = "site"
                }
            """.trimIndent())
        }
        testProject.run(WITH_BUILD_CACHE, HUGO_COMMAND).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(it.task(":$HUGO_COMMAND")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject}/site/config.toml")).isFile
        }
    }

    @Test
    fun `hugo should run hugo command provided in command option`() {
        testProject.run(WITH_BUILD_CACHE, HUGO_COMMAND, "--command=version").also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(it.task(":$HUGO_COMMAND")!!.outcome).isEqualTo(SUCCESS)
            assertThat(it.output).contains("hugo v$HUGO_VERSION")
        }
    }

    @Test
    fun `hugo should fail when command fails`() {
        testProject.runAndFail(WITH_BUILD_CACHE, HUGO_COMMAND, "--command=invalid").also {
            assertThat(it.task(":$HUGO_COMMAND")!!.outcome).isEqualTo(FAILED)
        }
    }
}

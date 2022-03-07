package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.*
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.HUGO_DOWNLOAD
import io.github.fstaudt.hugo.tasks.HugoServer.Companion.HUGO_SERVER
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome.FROM_CACHE
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.gradle.testkit.runner.TaskOutcome.FAILED
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HugoServerTest {

    private lateinit var testProject: TestProject

    @BeforeEach
    fun `init test project`() {
        testProject = testProject()
        testProject.initHugoResources()
    }

    @AfterEach
    fun `cleanup test project`() {
        testProject.deleteRecursively()
    }

    @Test
    fun `hugo should serve hugo site in base directory with default parameters`() {
        testProject.runAndFail(WITH_BUILD_CACHE, HUGO_SERVER, "--args=--forceFailure").also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(it.task(":$HUGO_SERVER")!!.outcome).isEqualTo(FAILED)
            assertThat(it.output).contains("Error: unknown flag: --forceFailure")
        }
    }

    @Test
    fun `hugo should serve hugo site in base directory on requested baseUrl`() {
        testProject.runAndFail(WITH_BUILD_CACHE, HUGO_SERVER, "--baseURL=http://localhost:1313/documentation", "--args=--forceFailure").also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(it.task(":$HUGO_SERVER")!!.outcome).isEqualTo(FAILED)
            assertThat(it.output).contains("Error: unknown flag: --forceFailure")
        }
    }

    @Test
    fun `hugo should serve hugo site in base directory with additional parameters`() {
        testProject.runAndFail(WITH_BUILD_CACHE, HUGO_SERVER, "--args=--port 1314 --forceFailure").also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(it.task(":$HUGO_SERVER")!!.outcome).isEqualTo(FAILED)
            assertThat(it.output).contains("Error: unknown flag: --forceFailure")
        }
    }
}
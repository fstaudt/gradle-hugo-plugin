package io.github.fstaudt.hugo.tasks

import io.github.fstaudt.hugo.HugoPluginExtension.Companion.SOURCE_DIRECTORY
import io.github.fstaudt.hugo.TestProject
import io.github.fstaudt.hugo.WITH_BUILD_CACHE
import io.github.fstaudt.hugo.buildDir
import io.github.fstaudt.hugo.initBuildFile
import io.github.fstaudt.hugo.initHugoResources
import io.github.fstaudt.hugo.run
import io.github.fstaudt.hugo.runAndFail
import io.github.fstaudt.hugo.tasks.HugoBuild.Companion.HUGO_BUILD
import io.github.fstaudt.hugo.tasks.HugoBuild.Companion.PUBLISH_DIRECTORY
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.HUGO_DOWNLOAD
import io.github.fstaudt.hugo.testProject
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome.FAILED
import org.gradle.testkit.runner.TaskOutcome.FROM_CACHE
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class HugoBuildTest {

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
    fun `hugoBuild should build new hugo site in output directory`() {
        testProject.run(WITH_BUILD_CACHE, HUGO_BUILD).also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(it.task(":$HUGO_BUILD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$PUBLISH_DIRECTORY/index.html")).isFile
            assertThat(File("${testProject.buildDir}/$PUBLISH_DIRECTORY/draft/index.html")).doesNotExist()
        }
    }

    @Test
    fun `hugoBuild should fail when site generation fails`() {
        File("$testProject/$SOURCE_DIRECTORY/config.toml").delete()
        testProject.runAndFail(WITH_BUILD_CACHE, HUGO_BUILD).also {
            assertThat(it.task(":$HUGO_BUILD")!!.outcome).isEqualTo(FAILED)
            assertThat(File("${testProject.buildDir}/$PUBLISH_DIRECTORY/index.html")).doesNotExist()
        }
    }

    @Test
    fun `hugoBuild should build new hugo site in publication path`() {
        testProject.initBuildFile {
            appendText("""
                tasks.withType<${HugoBuild::class.java.name}> {
                  publicationPath = "public/documentation"
                }
            """.trimIndent())
        }
        testProject.run(WITH_BUILD_CACHE, HUGO_BUILD).also {
            assertThat(it.task(":$HUGO_BUILD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$PUBLISH_DIRECTORY/public/documentation/index.html")).isFile
        }
    }

    @Test
    fun `hugoBuild should cleanup output directory`() {
        File("${testProject.buildDir}/$PUBLISH_DIRECTORY/outdated.html").apply {
            ensureParentDirsCreated()
            createNewFile()
        }
        testProject.run(WITH_BUILD_CACHE, HUGO_BUILD).also {
            assertThat(it.task(":$HUGO_BUILD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$PUBLISH_DIRECTORY/outdated.html")).doesNotExist()
        }
    }

    @Test
    fun `hugoBuild should retrieve output from cache when it was already executed`() {
        testProject.run(WITH_BUILD_CACHE, HUGO_BUILD).also {
            assertThat(it.task(":$HUGO_BUILD")!!.outcome).isEqualTo(SUCCESS)
        }
        File("${testProject.buildDir}/$PUBLISH_DIRECTORY").deleteRecursively()
        testProject.run(WITH_BUILD_CACHE, HUGO_BUILD).also {
            assertThat(it.task(":$HUGO_BUILD")!!.outcome).isEqualTo(FROM_CACHE)
            assertThat(File("${testProject.buildDir}/$PUBLISH_DIRECTORY/index.html")).isFile
        }
    }

    @Test
    fun `hugoBuild should take additional parameters into account`() {
        testProject.run(WITH_BUILD_CACHE, HUGO_BUILD, "--args=--buildDrafts").also {
            assertThat(it.task(":$HUGO_DOWNLOAD")!!.outcome).isIn(SUCCESS, FROM_CACHE)
            assertThat(it.task(":$HUGO_BUILD")!!.outcome).isEqualTo(SUCCESS)
            assertThat(File("${testProject.buildDir}/$PUBLISH_DIRECTORY/index.html")).isFile
            assertThat(File("${testProject.buildDir}/$PUBLISH_DIRECTORY/draft/index.html")).isFile
        }
    }
}

package io.github.fstaudt.hugo

import io.github.fstaudt.hugo.HugoPluginExtension.Companion.SOURCE_DIRECTORY
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

const val WITH_BUILD_CACHE = "--build-cache"

typealias TestProject = File

val TestProject.buildDir get() = File(this, "build")

fun testProject(parentFolder: File? = File("build/tmp")): TestProject {
    parentFolder?.mkdirs()

    return File.createTempFile("junit", "", parentFolder).apply {
        delete()
        mkdir()
        initSettingsFile()
        initBuildFile()
    }
}

private fun TestProject.initSettingsFile(): File {
    return File(this, "settings.gradle.kts").apply {
        writeText("rootProject.name = \"gradle-hugo-plugin-test\"")
    }
}

fun TestProject.initBuildFile(customizeBuildFile: File.() -> Unit = {}): File {
    return File(this, "build.gradle.kts").apply {
        writeText("""
                plugins {
                  id("io.github.fstaudt.hugo")
                }
            """.trimIndent())
        customizeBuildFile()
    }
}

fun TestProject.initHugoResources() {
    File("src/test/resources/hugo-resources").copyRecursively(File("$this/$SOURCE_DIRECTORY"))
    File("$this/$SOURCE_DIRECTORY/content/_index.md").writeText(name)
}

fun TestProject.run(vararg task: String): BuildResult {
    return gradleRunner(*task).build()
}

fun TestProject.runAndFail(vararg task: String): BuildResult {
    return gradleRunner(*task).buildAndFail()
}

private fun TestProject.gradleRunner(vararg task: String): GradleRunner {
    return GradleRunner.create()
        .withProjectDir(this)
        .withArguments("--info", "--stacktrace", *task)
        .withPluginClasspath()
        .withDebug(true)
        .forwardOutput()
}

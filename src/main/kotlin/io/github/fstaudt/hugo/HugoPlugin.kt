package io.github.fstaudt.hugo

import io.github.fstaudt.hugo.tasks.HugoBuild
import io.github.fstaudt.hugo.tasks.HugoBuild.Companion.HUGO_BUILD
import io.github.fstaudt.hugo.tasks.HugoCommand
import io.github.fstaudt.hugo.tasks.HugoCommand.Companion.HUGO_COMMAND
import io.github.fstaudt.hugo.tasks.HugoDownload
import io.github.fstaudt.hugo.tasks.HugoDownload.Companion.HUGO_DOWNLOAD
import io.github.fstaudt.hugo.tasks.HugoServer
import io.github.fstaudt.hugo.tasks.HugoServer.Companion.HUGO_SERVER
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class HugoPlugin : Plugin<Project> {

    companion object {
        const val HUGO = "hugo"
    }

    override fun apply(project: Project) {
        with(project) {
            val pluginExtension = extensions.create(HUGO, HugoPluginExtension::class)
            val hugoDownload = tasks.register<HugoDownload>(HUGO_DOWNLOAD) {
                group = HUGO
                description = "Download Hugo binary for the current OS (Windows, macOS or Linux)."
                extension = pluginExtension
            }
            tasks.register<HugoCommand>(HUGO_COMMAND) {
                group = HUGO
                description = "Execute any Hugo command (e.g. new, gen ...)."
                extension = pluginExtension
                dependsOn(hugoDownload)
            }
            tasks.register<HugoBuild>(HUGO_BUILD) {
                group = HUGO
                description = "Build Hugo static site for publication."
                extension = pluginExtension
                dependsOn(hugoDownload)
            }
            tasks.register<HugoServer>(HUGO_SERVER) {
                group = HUGO
                description = "Run server for development of Hugo static site."
                extension = pluginExtension
                dependsOn(hugoDownload)
            }
        }
    }
}

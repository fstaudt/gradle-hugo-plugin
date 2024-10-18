package io.github.fstaudt.hugo

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface HugoPluginExtension {
    @get:Input
    val version: Property<String>

    @get:Input
    val sourceDirectory: Property<String>

    @get:Input
    val windowsDownloadUrl: Property<String>

    @get:Input
    val linuxDownloadUrl: Property<String>

    @get:Input
    val macOSDownloadUrl: Property<String>

    @get:Input
    val osFamily: Property<OsFamily>
}

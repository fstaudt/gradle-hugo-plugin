## 0.12.0 - Dec 14, 2025

- :sparkles: add environment variables to hugo process ([#81](https://github.com/fstaudt/gradle-hugo-plugin/issues/81))
- :construction_worker: bump Gradle to 9.2.1
- :construction_worker: drop foojay-resolver-convention plugin

## 0.11.0 - Oct 25, 2025

- :sparkles: bump default Hugo version to 0.152.2
- :construction_worker: bump Gradle to 9.1.0

### Breaking changes

Gradle plugin requires at least Gradle 8.2.

## 0.10.0 - Apr 18, 2025

- :sparkles: bump default Hugo version to 0.144.0
- :construction_worker: bump Gradle to 8.13

## 0.9.0 - Oct 19, 2024

- :sparkles: reuse downloaded archive when it is still available
- :sparkles: bump default Hugo version to 0.136.2
- :sparkles: use Gradle provider API in all tasks
- :sparkles: reuse download task output to configure task dependencies lazily
- :construction_worker: bump Gradle to 8.10.2

### Breaking changes

Use of Gradle provider API in all tasks enforce usage of setters for properties for Gradle versions below 8.2.\
For Gradle versions above or equal to 8.2, tasks and extension properties can still be configured by assignment.\
More info: https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties

```kotlin
hugo {
    // version = "0.93.0"   // previously used - still OK for Gradle version above or equal to 8.2 
    version.set("0.93.0")   // required setter for Gradle version below 8.2
}
tasks.hugoBuild {
    // outputDirectory = File("$buildDir/hugo/publish")   // previously used - still OK for Gradle version above or equal to 8.2
    outputDirectory.set(File("$buildDir/hugo/publish"))   // required setter for Gradle version below 8.2
}
```

## 0.8.0 - Jun 15, 2024

- :sparkles: bump default Hugo version to 0.127.0
- :construction_worker: bump Java from 11 to 17
- :construction_worker: bump Gradle to 8.8

### Breaking changes

Gradle plugin requires at least Java 17.

## 0.7.1 - Apr 01, 2024

- :sparkles: bump default Hugo version to 0.124.1
- :construction_worker: bump Gradle to 8.7
- :white_check_mark: test against Gradle 7.6.4 and 8.0

## 0.7.0 - Dec 29, 2023

- :sparkles: make plugin compatible with configuration cache
- :sparkles: bump default hugo version to 0.121.1
- :bug: disable build cache for tasks with unpredictable outputs
- :construction_worker: bump gradle to 8.5
- :white_check_mark: register test suites for selected gradle versions

## 0.6.2 - Nov 07, 2023

- :sparkles: bump default hugo version to 0.120.3
- :construction_worker: bump gradle to 8.4
- :construction_worker: bump com.gradle.plugin-publish to 1.2.1

## 0.6.1 - Aug 28, 2023

- :sparkles: bump default hugo version to 0.117.0
- :construction_worker: bump gradle to 8.3
- :construction_worker: bump com.gradle.plugin-publish to 1.2.0

## 0.6.0 - Mar 26, 2023

- :sparkles: bump default hugo version to 0.111.3
- :construction_worker: bump gradle to 8.0.2
- :construction_worker: bump JVM toolchain to 11

## 0.5.1 - Jan 1, 2023

- :sparkles: bump default hugo version to 0.109.0
- :construction_worker: bump gradle to 7.6
- :construction_worker: bump com.gradle.plugin-publish to 1.1.0

## 0.5.0 - Oct 4, 2022

- :sparkles: bump default hugo version to 0.104.3
- :construction_worker: bump gradle to 7.5.1
- :construction_worker: bump com.gradle.plugin-publish to 1.0.0
- :construction_worker: GitHub workflows for build, release & dependabot

### Breaking changes

Default download URL have been updated to be compliant with Hugo versions above 0.103.0.

For older versions, download URL for the 3 OS must be overwritten in hugo extension to be compliant with the Hugo version:
```kotlin
hugo {
    version = "0.93.0"
    windowsDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_Windows-64bit.zip"
    linuxDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_Linux-64bit.tar.gz"
    macOSDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_macOS-64bit.tar.gz"
}
```

## 0.4.0 - May 7, 2022

- :sparkles: configurable directory for Hugo sources
- :sparkles: bump default hugo version to 0.98.0
- :construction_worker: bump gradle to 7.4.1

### Breaking changes

source directory for Hugo site defaults to `site` folder in project (previously `src/main/hugo`).

To revert to previous configuration, source directory can be overwritten in hugo extension:
```kotlin
hugo {
  sourceDirectory = "src/main/hugo"
}
```

## 0.3.0 - Mar 7, 2022

- :sparkles: hugoServer task to run server for development
- :sparkles: bump default hugo version to 0.93.2
- :bug: consistent configuration in hugoBuild for additional args
- :construction_worker: bump gradle to 7.4

## 0.2.0 - Nov 08, 2021

- :bug: avoid Gradle cache hits on hugoDownload between different operating systems

## 0.1.0 - Nov 08, 2021

- :sparkles: Hugo gradle plugin
  > - download Hugo binary with cache
  > - run any Hugo command with downloaded binary
  > - build static site with cache

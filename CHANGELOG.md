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

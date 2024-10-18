# Gradle Hugo plugin

![Build](https://github.com/fstaudt/gradle-hugo-plugin/workflows/Build/badge.svg)
![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.fstaudt.hugo)

![Minimal Java version](https://img.shields.io/badge/Minimal_Java_version-17-blue)
![Minimal Gradle version](https://img.shields.io/badge/Minimal_Gradle_version-7.6.4-blue)

Wrapper for [Hugo](https://gohugo.io) static site generator.

The plugin provides a declarative approach for the Hugo binary used to build the static site.\
The requested Hugo version is downloaded and cached by Gradle to ensure that your static site is built with the right version of Hugo.

## Extension configuration

Plugin extension is available to override general configuration (default values provided below).

```kotlin
hugo {
    // Hugo version
    version = "0.136.2"
    // Relative path to sources of Hugo site in Gradle project
    sourceDirectory = "site"
    // Download URL for Windows ( {0} can be used to replace version )
    windowsDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_windows-amd64.zip"
    // Download URL for Linux ( {0} can be used to replace version )
    linuxDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_linux-amd64.tar.gz"
    // Download URL for macOS ( {0} can be used to replace version )
    macOSDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_darwin-universal.tar.gz"
    // Operating system family (Windows, macOS or Unix)
    osFamily = io.github.fstaudt.hugo.OsFamily.CURRENT_SYSTEM // default value derived from system property "os.name"
}
```

## Tasks

### hugo

Execute any Hugo command (e.g. new, gen, check ...).

Option `command` allows to specify the Hugo command to execute.\
It defaults to `new site .`.

```shell
gradle hugo --command=check
```

### hugoServer

Run server for development of Hugo static site.

Task configuration can be overridden according to your needs (example values provided below).

```kotlin
tasks.hugoServer {
    // optional baseUrl to access Hugo static site in browser (defaults to baseUrl configured in config.toml)
    baseURL = "http://localhost:1313/documentation/"
    // optional additional server arguments (appended to arguments generated from baseUrl)
    args = "--buildDrafts --buildExpired"
}
```

:bulb: This task is configurable in build.gradle.kts and should be preferred to `hugo` task to serve Hugo static site.

### hugoBuild

Build Hugo static site for publication.

Task configuration can be overridden according to your needs (default values provided below).

```kotlin
tasks.hugoBuild {
    // Output directory for build result
    outputDirectory = File("$buildDir/hugo/publish")
    // additional path in output directory
    publicationPath = ""
    // optional additional build arguments (appended to "-d" argument generated from previous properties) 
    args = ""
}
```

:bulb: This task is cacheable and should be preferred to `hugo` task to build Hugo static site.

### hugoDownload

Download Hugo binary for the current OS (Windows, macOS or Linux).

By default, downloaded version is Hugo extended v0.117.0.\
Downloaded version can be configured in [Extension configuration](#extension-configuration).

This task is a dependency of the previous tasks.

## Examples

- [Generation of standalone Hugo site](https://github.com/fstaudt/gradle-hugo-plugin-examples)
- [Integration with Spring Boot](https://github.com/fstaudt/gradle-hugo-plugin-examples)

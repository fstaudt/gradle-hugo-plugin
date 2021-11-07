# gradle-hugo-plugin

Wrapper for [Hugo](https://gohugo.io) static site generator.

The plugin provides a declarative approach for the Hugo binary used to build the static site.\
The requested Hugo version is downloaded and cached by Gradle to ensure that your static site is built with the right version of Hugo.

## Extension configuration

Plugin extension is available to override general configuration (default values provided below).

```kotlin
hugo {
    // Hugo version
    version = "0.89.1"
    // Download URL for Windows (`{0}` can be used to replace version)
    windowsDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_Windows-64bit.zip"
    // Download URL for Linux (`{0}` can be used to replace version)
    linuxDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_Linux-64bit.tar.gz"
    // Download URL for macOS (`{0}` can be used to replace version)
    macOSDownloadUrl = "https://github.com/gohugoio/hugo/releases/download/v{0}/hugo_extended_{0}_macOS-64bit.tar.gz"
}
```


## Tasks

### hugo

Execute any Hugo command (e.g. new, serve ...).

Option `command` allows to specify the Hugo command to execute.\
It defaults to `new site .`.

```shell
gradle hugo --command=serve
```

### hugoBuild

Build Hugo static site for publication.\
Sources of Hugo site must be stored in `src/main/hugo`. 

Task configuration can be overridden according to your needs (default values provided below).

```kotlin
tasks.hugoBuild {
    // Output directory for build result
    outputDirectory = File("$buildDir/hugo/publish")
    // additional path in output directory
    publicationPath = ""
    // additional build arguments (concatenated to "-d" argument generated from outputDirectory and publicationPath) 
    arguments = emptyList()
}
```

:info: This task is cacheable and should be preferred to `hugo` task to build Hugo static site.

### hugoDownload

Download Hugo binary for the current OS (Windows, macOS or Linux).

By default, downloaded version is Hugo extended v0.89.1.\
Downloaded version can be configured in [Extension configuration](#extension-configuration).

This task is a dependency of the previous tasks.

## Integration with Spring Boot

Hugo gradle plugin can be used in combination with Spring Boot gradle plugin to serve a static website in a Spring Boot application.
```kotlin
plugins {
    java
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.springframework.boot") version "2.5.6"
    id("io.github.fstaudt.hugo") version "0.1.0"
}
tasks.hugoBuild {
    publicationPath = "static"
    sourceSets {
        main {
            resources {
                srcDir(outputDirectory)
            }
        }
    }
}
tasks.classes { dependsOn(tasks.hugoBuild) }
```

Since Hugo pages will be served as static resources, it is required to enable ugly URLs in Hugo configuration.
```toml
uglyUrls = true
```

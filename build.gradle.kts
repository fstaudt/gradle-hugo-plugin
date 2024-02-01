@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
    `maven-publish`
}

kotlin {
    jvmToolchain(11)
}

repositories {
    mavenCentral()
}

val pluginVersion = "$version"
val pluginName = "hugoPlugin"
gradlePlugin {
    website.set("https://github.com/fstaudt/gradle-hugo-plugin")
    vcsUrl.set("https://github.com/fstaudt/gradle-hugo-plugin")
    plugins {
        register(pluginName) {
            id = "$group"
            displayName = "Gradle Hugo plugin"
            description = "Build Hugo static sites with Gradle!"
            implementationClass = "$group.HugoPlugin"
            version = pluginVersion
            tags.set(listOf("gohugoio", "wrapper", "documentation", "spring-boot"))
        }
    }
}

dependencies {
    compileOnly(gradleKotlinDsl())
    api(kotlin("gradle-plugin"))
}

tasks.validatePlugins {
    enableStricterValidation = true
}

val currentGradleVersion: String = GradleVersion.current().version
val additionalGradleVersions = listOf("7.6.3")
val testGradleVersion = "testGradleVersion"
val displayNameSuffix = "displayNameSuffix"
testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
            dependencies {
                implementation(gradleTestKit())
                implementation("org.assertj:assertj-core:3.24.2")
                implementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
                implementation("io.mockk:mockk-jvm:1.13.9")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine")
            }
            targets {
                named("test") {
                    testTask {
                        systemProperties(testGradleVersion to currentGradleVersion, displayNameSuffix to "")
                    }
                }
                additionalGradleVersions.forEach { version ->
                    create("testWithGradle${version.replace(Regex("\\W"), "_")}") {
                        testTask {
                            systemProperties(testGradleVersion to version, displayNameSuffix to " - Gradle $version")
                            mustRunAfter(tasks.test)
                        }
                    }
                }
            }
        }
    }
}

val gradleWrapperVersion: String by project
tasks.wrapper {
    gradleVersion = gradleWrapperVersion
}

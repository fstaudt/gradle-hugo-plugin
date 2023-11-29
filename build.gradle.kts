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

// Register test suites for selected Gradle versions
val testedGradleVersions = listOf(
    "8.5",
    "8.0",
    "7.0",
)
testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
            dependencies {
                implementation(gradleTestKit())
                implementation("org.assertj:assertj-core:3.24.2")
                implementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
                implementation("io.mockk:mockk-jvm:1.13.8")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine")
            }
            targets {
                val testGradleVersionSysPropName = "testGradleVersion"
                val wrapperGradleVersion = GradleVersion.current().version
                named("test") {
                    testTask {
                        systemProperty("testGradleVersion", wrapperGradleVersion)
                    }
                }
                fun suiteNameFor(version: String): String {
                    return "gradle${version.replace('.', '_').replace('+', '_')}Test"
                }
                testedGradleVersions.minus(wrapperGradleVersion).forEach { testGradleVersion ->
                    create(suiteNameFor(testGradleVersion)) {
                        testTask {
                            systemProperty(testGradleVersionSysPropName, testGradleVersion)
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

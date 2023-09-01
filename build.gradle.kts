plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.0"
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
    testImplementation(gradleTestKit())
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("io.mockk:mockk-jvm:1.13.5")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

val gradleWrapperVersion: String by project
tasks.wrapper {
    gradleVersion = gradleWrapperVersion
}

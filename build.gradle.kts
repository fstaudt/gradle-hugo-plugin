import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.0.0"
    `maven-publish`
}

repositories {
    mavenCentral()
}

val pluginVersion = "$version"
val pluginName = "hugoPlugin"
gradlePlugin {
    plugins {
        register(pluginName) {
            id = "$group"
            implementationClass = "$group.HugoPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/fstaudt/gradle-hugo-plugin"
    vcsUrl = "https://github.com/fstaudt/gradle-hugo-plugin"
    description = "Build Hugo static sites with Gradle!"
    (plugins) {
        pluginName {
            displayName = "Gradle Hugo plugin"
            tags = listOf("gohugoio", "wrapper", "documentation", "spring-boot")
            version = pluginVersion
        }
    }
}

dependencies {
    compileOnly(gradleKotlinDsl())
    api(kotlin("gradle-plugin"))
    testImplementation(gradleTestKit())
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("io.mockk:mockk:1.12.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

java {
    targetCompatibility = VERSION_1_8
}
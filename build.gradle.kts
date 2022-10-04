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
            displayName = "Gradle Hugo plugin"
            description = "Build Hugo static sites with Gradle!"
            implementationClass = "$group.HugoPlugin"
            version = pluginVersion
        }
    }
}

pluginBundle {
    website = "https://github.com/fstaudt/gradle-hugo-plugin"
    vcsUrl = "https://github.com/fstaudt/gradle-hugo-plugin"
    tags = listOf("gohugoio", "wrapper", "documentation", "spring-boot")
}

dependencies {
    compileOnly(gradleKotlinDsl())
    api(kotlin("gradle-plugin"))
    testImplementation(gradleTestKit())
    testImplementation("org.assertj:assertj-core:3.23.1")
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

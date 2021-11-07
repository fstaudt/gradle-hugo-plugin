plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.17.0"
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
            tags = listOf("hugo", "gohugo", "wrapper", "static", "site", "documentation", "generation")
            version = pluginVersion
        }
    }
}

dependencies {
    compileOnly(gradleKotlinDsl())
    api(kotlin("gradle-plugin"))
    testImplementation(gradleTestKit())
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}

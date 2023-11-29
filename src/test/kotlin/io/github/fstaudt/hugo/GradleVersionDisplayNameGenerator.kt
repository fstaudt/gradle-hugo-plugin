package io.github.fstaudt.hugo

import org.junit.jupiter.api.DisplayNameGenerator
import java.lang.reflect.Method

/**
 * JUnit test name generator that appends the tested Gradle version to all test names.
 */
class GradleVersionDisplayNameGenerator : DisplayNameGenerator.Standard() {

    private val gradleVersion = gradleVersionSystemProp

    override fun generateDisplayNameForMethod(testClass: Class<*>, testMethod: Method): String {
        testMethod.name
        return "${testMethod.name} - Gradle $gradleVersion"
    }
}

package io.github.fstaudt.hugo.conditions

import io.github.fstaudt.hugo.conditions.ForGradleVersionCondition.Version.Companion.toVersion
import io.github.fstaudt.hugo.testGradleVersion
import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtensionContext

class ForGradleVersionCondition : ExecutionCondition {
    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        val annotation = context.element.get().getAnnotation(ForGradleVersion::class.java)
        val gradleVersion = testGradleVersion().toVersion()
        annotation.before.takeIf { it.isNotBlank() }?.toVersion()?.let { before ->
            if (before < gradleVersion) {
                return ConditionEvaluationResult.disabled("Version $gradleVersion is not before $before")
            }
        }
        annotation.equalToOrAfter.takeIf { it.isNotBlank() }?.toVersion()?.let { equalToOrAfter ->
            if (gradleVersion <= equalToOrAfter) {
                return ConditionEvaluationResult.disabled("Version $gradleVersion is not after or equal to $equalToOrAfter")
            }
        }
        return ConditionEvaluationResult.enabled("")
    }

    private data class Version(val major: Int, val minor: Int, val fix: Int) : Comparable<Version> {
        companion object {
            fun String.toVersion(): Version {
                return split('.').let {
                    Version(it.getAsInt(0), it.getAsInt(1), it.getAsInt(2))
                }
            }
            private fun List<String>.getAsInt(index: Int) = getOrNull(index)?.toIntOrNull() ?: 0
        }
        override fun compareTo(other: Version): Int {
            return compareValuesBy(this, other, Version::major, Version::minor, Version::fix)
        }
    }



}

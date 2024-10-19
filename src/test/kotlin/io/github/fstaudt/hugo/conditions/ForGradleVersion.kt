package io.github.fstaudt.hugo.conditions

import org.junit.jupiter.api.extension.ExtendWith
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(ForGradleVersionCondition::class)
@Inherited
annotation class ForGradleVersion(val below: String = "", val aboveOrEqualTo: String = "")

package com.kotlin.test.unit

import io.kotest.core.annotation.Tags
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@Tags("unit")
internal open class BaseUnitTest: DescribeSpec() {

    protected fun verifyException(
        actualException: Exception,
        expectedException: Exception,
    ) {
        actualException.message shouldBe expectedException.message
    }
}
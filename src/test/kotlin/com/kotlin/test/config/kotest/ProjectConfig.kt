package com.kotlin.test.config.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

internal object ProjectConfig: AbstractProjectConfig() {

    // 1 보다 큰 값이면 병렬로 테스트 실행, 이 숫자는 동시에 처리할 spec의 개수 (integration, unit 다르게 설정 할 수 있을지 ?)
    override val parallelism = 1

    override fun extensions() = listOf(SpringExtension)
}
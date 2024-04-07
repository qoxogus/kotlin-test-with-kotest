package com.kotlin.test.config.mock

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object MockMvcConfig {

    val defaultTestObjectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
}
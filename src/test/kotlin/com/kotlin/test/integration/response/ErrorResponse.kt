package com.kotlin.test.integration.response

import java.util.Date

class ErrorResponse(
    val timestamp: Date,
    val status: Int,
    val error: String,
    val path: String,
) {
}
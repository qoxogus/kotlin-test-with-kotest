package com.kotlin.test.constant.url

object UrlConstant {

    private const val BASE_API_URL = "/api"
    private const val BASE_VERSION1_URL = "/v1"

    private const val MEMBER_BASE_URL = "$BASE_API_URL$BASE_VERSION1_URL/members"

    const val MEMBER_TO_ADD_URL = MEMBER_BASE_URL
    const val MEMBER_TO_GET_BY_ID_URL = "$MEMBER_BASE_URL/{memberId}"
}
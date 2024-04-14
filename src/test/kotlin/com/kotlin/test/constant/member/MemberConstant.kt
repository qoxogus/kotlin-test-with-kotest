package com.kotlin.test.constant.member

import com.kotlin.test.dto.member.MemberDto
import com.kotlin.test.model.member.Member
import java.time.OffsetDateTime

object MemberConstant {

    const val NEW_MEMBER_EMAIL = "new.member@gamil.com"
    const val NEW_MEMBER_NAME = "new-member"

    const val EXISTING_MEMBER_ID: Long = 1
    const val EXISTING_MEMBER_EMAIL = "email"
    const val EXISTING_MEMBER_NAME = "name"

    const val NOT_FOUND_MEMBER_ID: Long = Long.MAX_VALUE
    const val NOT_FOUND_MEMBER_EMAIL: String = "not.found.member@gmail.com"

    const val EMAIL_FIELD = "email"
    const val NAME_FIELD = "name"

    val MOCK_MEMBER = Member(
        id = 1,
        email = NEW_MEMBER_EMAIL,
        name = NEW_MEMBER_NAME
    )

    val MOCK_MEMBER_DTO = MemberDto(
        id = MOCK_MEMBER.id!!,
        email = MOCK_MEMBER.email!!,
        name = MOCK_MEMBER.name!!,
        createdDateTime = OffsetDateTime.now(),
        updatedDateTime = OffsetDateTime.now(),
    )
}
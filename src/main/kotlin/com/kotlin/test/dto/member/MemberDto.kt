package com.kotlin.test.dto.member

import com.kotlin.test.model.member.Member
import com.querydsl.core.annotations.QueryProjection
import java.time.OffsetDateTime

class MemberDto @QueryProjection constructor(
        val id: Long,
        val email: String,
        val name: String,
        val createdDateTime: OffsetDateTime,
        val updatedDateTime: OffsetDateTime,
) {

    companion object {

        fun fromMember(member: Member) =
                MemberDto(
                        id = member.id!!,
                        email = member.email!!,
                        name = member.name!!,
                        createdDateTime = member.createdDateTime,
                        updatedDateTime = member.updatedDateTime,
                )
    }
}
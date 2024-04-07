package com.kotlin.test.datafetcher.member.mapper

import com.kotlin.test.dto.member.MemberDto
import com.kotlin.test.model.member.Member
import com.kotlin.test.generated.types.Member as MemberQL

object MemberMapper {

    fun fromMember(member: Member) =
            MemberQL(
                    member.id!!,
                    member.email!!,
                    member.name!!,
                    member.createdDateTime,
                    member.updatedDateTime,
            )

    fun fromMemberDto(memberDto: MemberDto) =
            MemberQL(
                    memberDto.id,
                    memberDto.email,
                    memberDto.name,
                    memberDto.createdDateTime,
                    memberDto.updatedDateTime,
            )
}
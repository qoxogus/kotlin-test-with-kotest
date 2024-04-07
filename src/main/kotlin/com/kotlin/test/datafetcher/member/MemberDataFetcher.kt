package com.kotlin.test.datafetcher.member

import com.kotlin.test.datafetcher.member.mapper.MemberMapper
import com.kotlin.test.generated.types.MemberToAddInput
import com.kotlin.test.model.member.Member
import com.kotlin.test.service.MemberService
import com.kotlin.test.util.alsoIfTrue
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import java.lang.IllegalArgumentException
import com.kotlin.test.generated.types.Member as MemberQL

@DgsComponent
class MemberDataFetcher(
        private val memberService: MemberService,
) {

    @DgsMutation
    fun addMember(@InputArgument input: MemberToAddInput): MemberQL {
        verifyForAddingMember(input)

        return memberService.save(member = Member.fromMemberToAddInput(input))
                .let {
                    MemberMapper.fromMember(member = it)
                }
    }

    private fun verifyForAddingMember(input: MemberToAddInput) {
        memberService.existsByEmail(email = input.email)
                .alsoIfTrue { throw IllegalArgumentException("Already Exists Email") }
    }

    @DgsQuery
    fun member(@InputArgument memberId: Long): MemberQL {
        return memberService.getMemberDtoById(memberId)
                .let {
                    MemberMapper.fromMemberDto(memberDto = it)
                }
    }
}
package com.kotlin.test.service

import com.kotlin.test.dto.member.MemberDto
import com.kotlin.test.model.member.Member
import com.kotlin.test.repository.member.MemberRepository
import com.kotlin.test.repository.member.QMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
        private val memberRepository: MemberRepository,
        private val qMemberRepository: QMemberRepository,
) {

    @Transactional(rollbackFor = [Exception::class])
    fun save(member: Member): Member {
        return memberRepository.save(member)
    }

    @Transactional(readOnly = true)
    fun existsByEmail(email: String): Boolean {
        return qMemberRepository.existsByEmail(email)
    }

    @Transactional(readOnly = true)
    fun getMemberDtoById(memberId: Long): MemberDto {
        return qMemberRepository.findById(memberId) ?: throw IllegalArgumentException("Not Found Member")
    }
}
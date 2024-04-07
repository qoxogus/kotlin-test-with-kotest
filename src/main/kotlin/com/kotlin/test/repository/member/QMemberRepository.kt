package com.kotlin.test.repository.member

import com.kotlin.test.dto.member.MemberDto
import com.kotlin.test.dto.member.QMemberDto
import com.kotlin.test.model.member.QMember.*
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class QMemberRepository(
        private val queryFactory: JPAQueryFactory,
) {

    fun findById(memberId: Long): MemberDto? {
        return queryFactory
                .select(makeQMemberDto())
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne()
    }

    private fun makeQMemberDto() = QMemberDto(
            member.id,
            member.email,
            member.name,
            member.createdDateTime,
            member.updatedDateTime,
    )

    fun existsByEmail(email: String): Boolean {
        return queryFactory
                .select(member.id)
                .from(member)
                .where(member.email.eq(email))
                .fetchOne() != null
    }
}
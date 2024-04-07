package com.kotlin.test.repository.member

import com.kotlin.test.model.member.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {
}
package com.kotlin.test.unit.member

import com.kotlin.test.constant.exception.ExceptionConstant
import com.kotlin.test.constant.member.MemberConstant
import com.kotlin.test.dto.member.MemberDto
import com.kotlin.test.model.member.Member
import com.kotlin.test.repository.member.MemberRepository
import com.kotlin.test.repository.member.QMemberRepository
import com.kotlin.test.service.MemberService
import com.kotlin.test.unit.BaseUnitTest
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javax.persistence.PersistenceException

internal class MemberUnitTest: BaseUnitTest() {

    init {
        val memberMockRepository = mockk<MemberRepository>()
        val qMemberMockRepository = mockk<QMemberRepository>()

        val memberService = MemberService(memberMockRepository, qMemberMockRepository)

        describe("[Unit] memberService.save()") {
            context("아무런 예외도 발생하지 않는다면") {
                every { memberMockRepository.save(any<Member>()) } returns MemberConstant.MOCK_MEMBER

                it("정상적으로 저장된다") {
                    memberService.save(MemberConstant.MOCK_MEMBER)

                    verify(exactly = 1) {
                        memberMockRepository.save(any<Member>())
                    }
                }
            }

            context("memberRepository.save() 호출 시 PersistenceException 예외가 발생한다면") {
                every { memberMockRepository.save(any<Member>()) } throws ExceptionConstant.PERSISTENCE_EXCEPTION

                it("저장되지 않는다") {
                    val persistenceException = shouldThrowExactly<PersistenceException> {
                        memberService.save(MemberConstant.MOCK_MEMBER)
                    }

                    verifyException(
                        actualException = persistenceException,
                        expectedException = ExceptionConstant.PERSISTENCE_EXCEPTION
                    )

                    verify(exactly = 1) {
                        memberMockRepository.save(any<Member>())
                    }
                }
            }
        }

        describe("[Unit] memberService.existsByEmail()") {
            context("이미 존재하는 이메일이라면") {
                every { qMemberMockRepository.existsByEmail(any<String>()) } returns true

                it("true를 반환한다") {
                    val isExistsEmail = memberService.existsByEmail(MemberConstant.EXISTING_MEMBER_EMAIL)

                    verifyExistsEmail(
                        actualExistsEmail = isExistsEmail,
                        expectedExistsEmail = true
                    )

                    verify(exactly = 1) {
                        qMemberMockRepository.existsByEmail(any<String>())
                    }
                }
            }

            context("존재하지 않는 이메일이라면") {
                every { qMemberMockRepository.existsByEmail(any<String>()) } returns false

                it("false를 반환한다") {
                    val isExistsEmail = memberService.existsByEmail(MemberConstant.NOT_FOUND_MEMBER_EMAIL)

                    verifyExistsEmail(
                        actualExistsEmail = isExistsEmail,
                        expectedExistsEmail = false
                    )

                    verify(exactly = 1) {
                        qMemberMockRepository.existsByEmail(any<String>())
                    }
                }
            }
        }

        describe("[Unit] memberService.getMemberDtoById") {
            context("존재하는 회원이라면") {
                every { qMemberMockRepository.findById(any<Long>()) } returns MemberConstant.MOCK_MEMBER_DTO

                it("MemberDto를 반환한다") {
                    val actualMemberDto = memberService.getMemberDtoById(MemberConstant.EXISTING_MEMBER_ID)

                    verifyMemberDto(
                        actualMemberDto = actualMemberDto,
                        expectedMemberDto = MemberConstant.MOCK_MEMBER_DTO
                    )

                    verify(exactly = 1) {
                        qMemberMockRepository.findById(any<Long>())
                    }
                }
            }

            context("존재하지 않는 회원이라면") {
                every { qMemberMockRepository.findById(any<Long>()) } throws ExceptionConstant.MEMBER_NOT_FOUND_EXCEPTION

                it("IllegalArgumentException(Not Found Member)을 반환한다") {
                    val actualException = shouldThrowExactly<IllegalArgumentException> {
                        memberService.getMemberDtoById(MemberConstant.NOT_FOUND_MEMBER_ID)
                    }

                    verifyException(
                        actualException = actualException,
                        expectedException = ExceptionConstant.MEMBER_NOT_FOUND_EXCEPTION
                    )

                    verify(exactly = 1) {
                        qMemberMockRepository.findById(any<Long>())
                    }
                }
            }
        }
    }

    private fun verifyMemberDto(actualMemberDto: MemberDto, expectedMemberDto: MemberDto) {
        actualMemberDto shouldBe expectedMemberDto
    }

    private fun verifyExistsEmail(
        actualExistsEmail: Boolean,
        expectedExistsEmail: Boolean,
    ) {
        actualExistsEmail shouldBe expectedExistsEmail
    }
}
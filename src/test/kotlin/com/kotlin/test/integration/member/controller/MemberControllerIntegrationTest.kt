package com.kotlin.test.integration.member.controller

import com.kotlin.test.constant.exception.ExceptionConstant
import com.kotlin.test.constant.exception.ExceptionMessageConstant
import com.kotlin.test.constant.member.MemberConstant
import com.kotlin.test.constant.url.UrlConstant
import com.kotlin.test.dto.member.MemberDto
import com.kotlin.test.integration.BaseIntegrationTest
import com.kotlin.test.integration.response.ErrorResponse
import com.kotlin.test.util.andReturnAs
import com.kotlin.test.util.expectStatus
import com.kotlin.test.util.getWithPathVariable
import com.kotlin.test.util.postWithBody
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.http.HttpStatus
import org.springframework.web.util.NestedServletException

internal class MemberControllerIntegrationTest: BaseIntegrationTest() {

    init {
        describe("[REST | POST] addMember (/api/v1/members)") {
            val memberToAddUrl = UrlConstant.MEMBER_TO_ADD_URL

            val keys = makeKeys(
                MemberConstant.EMAIL_FIELD,
                MemberConstant.NAME_FIELD,
            )

            context("존재하지 않는 이메일이라면") {
                val values = makeValues(
                    MemberConstant.NEW_MEMBER_EMAIL,
                    MemberConstant.NEW_MEMBER_NAME,
                )

                it("정상적으로 등록된다") {
                    val memberDto = mockMvc.postWithBody(
                            url = memberToAddUrl,
                            body = makeRequestJsonObject(keys, values))
                            .expectStatus(HttpStatus.CREATED)
                            .andReturnAs<MemberDto>()

                    verifyMemberDto(
                        actualMemberDto = memberDto,
                        expectedEmail = MemberConstant.NEW_MEMBER_EMAIL,
                        expectedName = MemberConstant.NEW_MEMBER_NAME,
                    )
                }
            }

            context("이미 존재하는 이메일이라면") {
                val values = makeValues(
                    MemberConstant.EXISTING_MEMBER_EMAIL,
                    MemberConstant.EXISTING_MEMBER_NAME
                )

                it("IllegalArgumentException(Already Exists Email)이 발생한다") {
                    val actualException = shouldThrowExactly<NestedServletException> {
                        val internalServerError = HttpStatus.INTERNAL_SERVER_ERROR

                        val errorResponse = mockMvc.postWithBody(
                            url = memberToAddUrl,
                            body = makeRequestJsonObject(keys, values))
                            .expectStatus(internalServerError)
                            .andReturnAs<ErrorResponse>()

                        verifyRestErrorResponse(
                            actualErrorResponse = errorResponse,
                            expectedHttpStatus = internalServerError,
                            path = memberToAddUrl
                        )
                    }

                    verifyException(
                        actualException = actualException,
                        expectedException = ExceptionConstant.MEMBER_ALREADY_EXISTS_EXCEPTION
                    )
                }
            }
        }

        describe("[REST | GET] getById (/api/v1/members/{memberId})") {
            val memberToGetByIdUrl = UrlConstant.MEMBER_TO_GET_BY_ID_URL

            context("존재하는 회원이라면") {
                val existingMemberId = MemberConstant.EXISTING_MEMBER_ID

                it("정상적으로 조회된다") {
                    val memberDto = mockMvc.getWithPathVariable(
                        url = memberToGetByIdUrl,
                        pathVariable = existingMemberId
                    )
                        .expectStatus(HttpStatus.OK)
                        .andReturnAs<MemberDto>()

                    verifyMemberDto(
                        actualMemberDto = memberDto,
                        expectedEmail = MemberConstant.EXISTING_MEMBER_EMAIL,
                        expectedName = MemberConstant.EXISTING_MEMBER_NAME,
                    )
                }
            }

            context("존재하지 않는 회원이라면") {
                val notFoundMemberId = MemberConstant.NOT_FOUND_MEMBER_ID

                it("IllegalArgumentException(Not Found Member)이 발생한다") {
                    val actualException = shouldThrowExactly<NestedServletException> {
                        val internalServerError = HttpStatus.INTERNAL_SERVER_ERROR

                        val errorResponse = mockMvc.getWithPathVariable(
                            url = memberToGetByIdUrl,
                            pathVariable = notFoundMemberId
                        )
                            .expectStatus(internalServerError)
                            .andReturnAs<ErrorResponse>()

                        verifyRestErrorResponse(
                            actualErrorResponse = errorResponse,
                            expectedHttpStatus = internalServerError,
                            path = memberToGetByIdUrl
                        )
                    }

                    verifyException(
                        actualException = actualException,
                        expectedException = ExceptionConstant.MEMBER_NOT_FOUND_EXCEPTION
                    )
                }
            }
        }
    }

    private fun verifyMemberDto(actualMemberDto: MemberDto, expectedEmail: String, expectedName: String) {
        actualMemberDto.id shouldNotBe null
        actualMemberDto.email shouldBe expectedEmail
        actualMemberDto.name shouldBe expectedName
        actualMemberDto.createdDateTime shouldNotBe null
        actualMemberDto.updatedDateTime shouldNotBe null
    }
}
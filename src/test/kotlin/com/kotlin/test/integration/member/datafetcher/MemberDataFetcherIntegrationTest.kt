package com.kotlin.test.integration.member.datafetcher

import com.kotlin.test.constant.error.ErrorTypeConstant
import com.kotlin.test.constant.exception.ExceptionConstant
import com.kotlin.test.constant.member.MemberConstant
import com.kotlin.test.generated.client.AddMemberGraphQLQuery
import com.kotlin.test.generated.client.AddMemberProjectionRoot
import com.kotlin.test.generated.client.MemberGraphQLQuery
import com.kotlin.test.generated.client.MemberProjectionRoot
import com.kotlin.test.generated.types.Member
import com.kotlin.test.generated.types.MemberToAddInput
import com.kotlin.test.integration.BaseIntegrationTest
import com.kotlin.test.util.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class MemberDataFetcherIntegrationTest: BaseIntegrationTest() {

    init {
        describe("[Mutation] addMember") {

            val memberToAddProjectionRoot = AddMemberProjectionRoot<Nothing, Nothing>()
                .id()
                .email()
                .name()
                .createdDateTime()
                .updatedDateTime()

            context("존재하지 않는 이메일이라면") {
                val memberToAddInput = MemberToAddInput(
                    MemberConstant.NEW_MEMBER_EMAIL,
                    MemberConstant.NEW_MEMBER_NAME,
                )

                val graphQLQuery = makeAddMemberGraphQLQuery(memberToAddInput)

                it("정상적으로 등록된다") {

                    val member = dgsQueryExecutor.executeQueryAndExtractJsonPathAsObject(
                        graphQLQueryRequest = makeGraphQLQueryRequest(
                            graphQLQuery = graphQLQuery,
                            baseProjectionNode = memberToAddProjectionRoot
                        ),
                        destination = graphQLQuery.getOperationName(),
                        clazz = Member::class.java
                    )

                    verifyMember(
                        actualMember = member,
                        expectedMemberEmail = MemberConstant.NEW_MEMBER_EMAIL,
                        expectedMemberName = MemberConstant.NEW_MEMBER_NAME
                    )
                }
            }

            context("존재하는 이메일이라면") {
                val memberToAddInput = MemberToAddInput(
                    MemberConstant.EXISTING_MEMBER_EMAIL,
                    MemberConstant.EXISTING_MEMBER_NAME,
                )

                val graphQLQuery = makeAddMemberGraphQLQuery(memberToAddInput)

                it("IllegalArgumentException(Already Exists Email)이 발생한다") {
                    val graphQLErrorResponse = dgsQueryExecutor.executeQuery(
                        graphQLQueryRequest = makeGraphQLQueryRequest(
                            graphQLQuery = graphQLQuery,
                            baseProjectionNode = memberToAddProjectionRoot
                        )
                    ).getErrorResponse()

                    verifyGraphQLErrorResponse(
                        graphQLErrorResponse = graphQLErrorResponse,
                        graphQLQuery = graphQLQuery,
                        expectedErrorType = ErrorTypeConstant.EXTENSIONS_ERROR_TYPE_INTERNAL,
                        expectedException = ExceptionConstant.MEMBER_ALREADY_EXISTS_EXCEPTION,
                    )
                }
            }
        }

        describe("[Query] getById") {
            val memberProjectionRoot = MemberProjectionRoot<Nothing, Nothing>()
                .id()
                .email()
                .name()
                .createdDateTime()
                .updatedDateTime()

            context("존재하는 회원이라면") {
                val existingMemberId = MemberConstant.EXISTING_MEMBER_ID

                val graphQLQuery = makeMemberGraphQLQuery(existingMemberId)

                it("정상적으로 조회된다") {
                    val member = dgsQueryExecutor.executeQueryAndExtractJsonPathAsObject(
                        graphQLQueryRequest = makeGraphQLQueryRequest(
                            graphQLQuery = graphQLQuery,
                            baseProjectionNode = memberProjectionRoot
                        ),
                        destination = graphQLQuery.getOperationName(),
                        clazz = Member::class.java
                    )

                    verifyMember(
                        actualMember = member,
                        expectedMemberEmail = MemberConstant.EXISTING_MEMBER_EMAIL,
                        expectedMemberName = MemberConstant.EXISTING_MEMBER_NAME,
                    )
                }
            }

            context("존재하지 않는 회원이라면") {
                val notFoundMemberId = MemberConstant.NOT_FOUND_MEMBER_ID

                val graphQLQuery = makeMemberGraphQLQuery(notFoundMemberId)

                it("IllegalArgumentException(Not Found Member)이 발생한다") {
                    val graphQLErrorResponse = dgsQueryExecutor.executeQuery(
                        graphQLQueryRequest = makeGraphQLQueryRequest(
                            graphQLQuery = graphQLQuery,
                            baseProjectionNode = memberProjectionRoot
                        )
                    ).getErrorResponse()

                    verifyGraphQLErrorResponse(
                        graphQLErrorResponse = graphQLErrorResponse,
                        graphQLQuery = graphQLQuery,
                        expectedErrorType = ErrorTypeConstant.EXTENSIONS_ERROR_TYPE_INTERNAL,
                        expectedException = ExceptionConstant.MEMBER_NOT_FOUND_EXCEPTION,
                    )
                }
            }
        }
    }

    private fun verifyMember(
        actualMember: Member,
        expectedMemberEmail: String,
        expectedMemberName: String
    ) {
        actualMember.id shouldNotBe null
        actualMember.email shouldBe expectedMemberEmail
        actualMember.name shouldBe expectedMemberName
        actualMember.createdDateTime shouldNotBe null
        actualMember.updatedDateTime shouldNotBe null
    }

    private fun makeAddMemberGraphQLQuery(memberToAddInput: MemberToAddInput): AddMemberGraphQLQuery {
        return AddMemberGraphQLQuery.newRequest()
            .input(memberToAddInput)
            .build()
    }

    private fun makeMemberGraphQLQuery(memberId: Long): MemberGraphQLQuery {
        return MemberGraphQLQuery.newRequest()
            .memberId(memberId)
            .build()
    }
}
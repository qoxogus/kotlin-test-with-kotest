package com.kotlin.test.integration

import com.kotlin.test.constant.encoding.EncodingConstant
import com.kotlin.test.constant.error.ErrorTypeConstant
import com.kotlin.test.integration.response.ErrorResponse
import com.kotlin.test.integration.response.GraphQLErrorResponse
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode
import com.netflix.graphql.dgs.client.codegen.GraphQLQuery
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import graphql.ExecutionResult
import io.kotest.core.annotation.Tags
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import java.util.HashMap

@Tags("integration")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
internal class BaseIntegrationTest: DescribeSpec() {

    @Autowired protected lateinit var mockMvc: MockMvc
    @Autowired protected lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Autowired private lateinit var webApplicationContext: WebApplicationContext

    override fun beforeSpec(spec: Spec) {
        setMockMvc()
    }

    private fun setMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter(EncodingConstant.UTF_8, true))
//                .apply<DefaultMockMvcBuilder>(springSecurity())
                .alwaysDo<DefaultMockMvcBuilder> { MockMvcResultHandlers.print() }
                .build()
    }

    protected fun verifyRestErrorResponse(
        actualErrorResponse: ErrorResponse,
        expectedHttpStatus: HttpStatus,
        path: String,
    ) {
        actualErrorResponse.timestamp shouldNotBe null
        actualErrorResponse.status shouldBe expectedHttpStatus.value()
        actualErrorResponse.error shouldBe expectedHttpStatus.reasonPhrase
        actualErrorResponse.path shouldBe path
    }

    protected fun verifyGraphQLErrorResponse(
        graphQLErrorResponse: GraphQLErrorResponse,
        graphQLQuery: GraphQLQuery,
        expectedErrorType: String,
        expectedException: Exception,
    ) {
        val graphQLError = graphQLErrorResponse.error.first()

        graphQLError.message shouldBe Throwable(expectedException).cause.toString()
        graphQLError.locations shouldBe emptyList()
        removePathBrackets(graphQLError.path) shouldBe graphQLQuery.getOperationName()
        graphQLError.extensions[ErrorTypeConstant.ERROR_TYPE_FIELD] shouldBe expectedErrorType
        graphQLErrorResponse.data.toString() shouldContain graphQLQuery.getOperationName()
        graphQLErrorResponse.isDataPresent shouldBe true
    }

    private fun removePathBrackets(path: List<Any>) = path.toString().trimStart('[').trimEnd(']')

    protected fun verifyException(actualException: Exception, expectedException: Exception) {
        actualException.cause shouldBe Throwable(expectedException).cause
    }

    protected fun makeGraphQLQueryRequest(
            graphQLQuery: GraphQLQuery,
            baseProjectionNode: BaseProjectionNode
    ) = GraphQLQueryRequest(graphQLQuery, baseProjectionNode)

    protected fun <K> makeKeys(vararg keys: K): List<K> {
        return listOf(*keys)
    }

    protected fun <V> makeValues(vararg values: V): List<V> {
        return listOf(*values)
    }

    protected fun <K, V> makeRequestJsonObject(keys: List<K>, values: List<V>): JSONObject {
        require(keys.size == values.size)
        // key에 해당하는 value인지는 검증 할 수 없음

        val requestParamsMap: MutableMap<K, V> = HashMap()

        for (i in keys.indices) {
            requestParamsMap[keys[i]] = values[i]
        }

        return JSONObject(requestParamsMap)
    }

    protected fun <K, V> makeRequestParamsMap(keys: List<K>, values: List<V>): MultiValueMap<K, V> {
        require(keys.size == values.size)
        // key에 해당하는 value인지는 검증 할 수 없음

        val requestParamsMap: MultiValueMap<K, V> = LinkedMultiValueMap()

        for (i in keys.indices) {
            requestParamsMap.add(keys[i]!!, values[i])
        }

        return requestParamsMap
    }
}
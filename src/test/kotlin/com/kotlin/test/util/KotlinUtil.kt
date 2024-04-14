package com.kotlin.test.util

import com.fasterxml.jackson.core.type.TypeReference
import com.jayway.jsonpath.TypeRef
import com.kotlin.test.config.mock.MockMvcConfig
import com.kotlin.test.constant.json.GraphQLJsonPathConstant
import com.kotlin.test.integration.response.GraphQLErrorResponse
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import graphql.ExecutionResult
import mu.KLogger
import mu.KotlinLogging
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.MultiValueMap
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

val logger: KLogger = KotlinLogging.logger {}

fun ResultActions.expectStatus(status: HttpStatus) = this.andExpect(MockMvcResultMatchers.status().`is`(status.value()))

inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

fun <BODY : Any> MockMvc.postWithBody(
        url: String,
        body: BODY,
        contentType: MediaType = MediaType.APPLICATION_JSON
): ResultActions =
    MockMvcRequestBuilders.post(url)
            .contentType(contentType)
            .content(body.toString())
            .run { perform(this) }

fun MockMvc.postWithParams(
        url: String,
        params: MultiValueMap<String, String>,
): ResultActions =
    MockMvcRequestBuilders.post(url)
            .params(params)
            .run { perform(this) }

fun <BODY : Any> MockMvc.postWithPathVariableAndBody(
        url: String,
        pathVariable: Any,
        body: BODY,
        contentType: MediaType = MediaType.APPLICATION_JSON,
): ResultActions =
    MockMvcRequestBuilders.post(url, pathVariable)
            .contentType(contentType)
            .content(body.toString())
            .run { perform(this) }

fun MockMvc.getWithPathVariable(
    url: String,
    pathVariable: Any,
): ResultActions =
    MockMvcRequestBuilders.get(url, pathVariable)
        .run { perform(this) }

inline fun <reified T> ParameterizedTypeReference<T>.toJacksonTypeRef(): TypeReference<T> {
    val type: Type = this.type
    return object : TypeReference<T>() {
        override fun getType(): Type = type
    }
}

inline fun <reified T> ResultActions.andReturnAs(): T =
    kotlin.runCatching {
        this.run {
            MockMvcConfig.defaultTestObjectMapper.readValue(
                    andReturn().response.getContentAsString(StandardCharsets.UTF_8),
                    typeReference<T>().toJacksonTypeRef()
            )
        }
    }.onSuccess {
        this.andDo(MockMvcResultHandlers.print())
    }.onFailure {
        this.andDo(MockMvcResultHandlers.print())
    }.getOrElse {
        throw IllegalArgumentException("failed response mapping string to object", it)
    }

fun DgsQueryExecutor.executeQuery(graphQLQueryRequest: GraphQLQueryRequest): ExecutionResult {
    return this.execute(graphQLQueryRequest.serialize())
}

inline fun <reified T> DgsQueryExecutor.executeQueryAndExtractJsonPathAsObject(
    graphQLQueryRequest: GraphQLQueryRequest,
    destination: String,
    clazz: Class<T>
): T {
    return this.executeAndExtractJsonPathAsObject(
        graphQLQueryRequest.serialize(),
        "${GraphQLJsonPathConstant.BASE_JSON_PATH}$destination",
        clazz
    )
}

inline fun <reified T> DgsQueryExecutor.executeQueryAndExtractJsonPathAsObject(
    graphQLQueryRequest: GraphQLQueryRequest,
    destination: String,
    typeRef: TypeRef<T>
): T {
    return this.executeAndExtractJsonPathAsObject(
        graphQLQueryRequest.serialize(),
        "${GraphQLJsonPathConstant.BASE_JSON_PATH}$destination",
        typeRef
    )
}

fun ExecutionResult.getGraphQLErrorResponse(): GraphQLErrorResponse =
    kotlin.runCatching {
        this.run {
            GraphQLErrorResponse(
                error = this.errors,
                data = this.getData(),
                isDataPresent = this.isDataPresent,
                extensions = this.extensions?.toMap(),
            )
        }
    }.onSuccess {
        logger.info { it }
    }.onFailure {
        logger.error { this.errors.first().toString() }
    }.getOrElse {
        throw IllegalArgumentException("failed response mapping executionResult to object", it)
    }
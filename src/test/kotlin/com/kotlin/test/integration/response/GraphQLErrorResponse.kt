package com.kotlin.test.integration.response

import graphql.GraphQLError

data class GraphQLErrorResponse (
    val error: List<GraphQLError>,
    val data: Any,
    val isDataPresent: Boolean,
    val extensions: Map<Any, Any>?,
) {
}
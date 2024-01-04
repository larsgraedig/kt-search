package com.jillesvangurp.ktsearch

import com.jillesvangurp.searchdsls.SearchEngineVariant.ES8
import com.jillesvangurp.searchdsls.VariantRestriction
import com.jillesvangurp.searchdsls.querydsl.ReindexDSL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReindexResponse(
    val took: Int,
    @SerialName("timed_out")
    val timedOut: Boolean,
    val total: Int,
    val updated: Int,
    val created: Int,
    val deleted: Int,
    val batches: Int,
    @SerialName("version_conflicts")
    val versionConflicts: Int,
    val noops: Int,
    val retries: ReindexRetries,
    @SerialName("throttled_millis")
    val throttledMillis: Int,
    @SerialName("requests_per_second")
    val requestsPerSecond: Double,
    @SerialName("throttled_until_millis")
    val throttledUntilMillis: Int,
    val failures: List<String>
)

@Serializable
data class ReindexRetries(val bulk: Int, val search: Int)

@VariantRestriction(ES8)
suspend fun SearchClient.reindex(
    refresh: Boolean? = null,
    timeout: String? = null,
    waitForActiveShards: Boolean? = null,
    waitForCompletion: Boolean? = null,
    requestsPerSecond: Int? = null,
    requireAlias: Boolean? = null,
    scroll: String? = null,
    slices: Int? = null,
    maxDocs: Int? = null,
    block: ReindexDSL.() -> Unit
): ReindexResponse {
    val reindexDSL = ReindexDSL()
    block(reindexDSL)

    return restClient.post {
        path("_reindex")
        parameter("refresh", refresh)
        parameter("timeout", timeout)
        parameter("wait_for_active_shards", waitForActiveShards)
        parameter("wait_for_completion", waitForCompletion)
        parameter("requests_per_second", requestsPerSecond)
        parameter("require_alias", requireAlias)
        parameter("scroll", scroll)
        parameter("slices", slices)
        parameter("max_docs", maxDocs)
        body = reindexDSL.toString()
    }.parse(ReindexResponse.serializer())
}
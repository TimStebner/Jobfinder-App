package de.jobfinder.app.core.model

import kotlinx.serialization.Serializable

data class SearchFilters(
    val radiusKm: Int = 25,
    val sources: Set<JobSource> = JobSource.activeSearchSources,
    val minSalary: Int? = null,
    val employmentTypes: Set<EmploymentType> = emptySet(),
    val remoteOnly: Boolean = false
)

data class SearchQuery(
    val query: String,
    val location: String = "Berlin",
    val filters: SearchFilters = SearchFilters()
)

data class QueryProcessing(
    val originalQuery: String,
    val effectiveQuery: String,
    val correctionApplied: Boolean = false,
    val matchedOccupation: String? = null,
    val equivalentVariants: List<String> = emptyList(),
    val relatedVariants: List<String> = emptyList(),
    val version: Int = 1
) {
    fun correctionNotice(currentQuery: String): String? =
        if (version == 1 && correctionApplied && originalQuery == currentQuery && effectiveQuery.isNotBlank())
            "Gesucht nach $effectiveQuery" else null
}

@Serializable
data class RecentSearch(
    val id: String,
    val query: String,
    val location: String,
    val radiusKm: Int,
    val isRemote: Boolean = false,
    val searchId: String? = null,
    val sources: Set<JobSource> = JobSource.activeSearchSources,
    val minSalary: Int? = null,
    val employmentTypes: Set<EmploymentType> = emptySet(),
)

enum class SourceSearchStatus {
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED,
    UNKNOWN
}

data class SourceSearchState(
    val source: JobSource,
    val status: SourceSearchStatus,
    val resultsSeen: Int = 0,
    val errorCode: String? = null,
    val pagesCompleted: Int = 0,
    val completionReason: String? = null,
    val limitReached: Boolean = false,
    val resultsComplete: Boolean? = null,
)

data class SearchResult(
    val searchId: String,
    val jobs: List<Job>,
    val loadedCount: Int,
    val totalCount: Int?,
    val hasNextPage: Boolean,
    val isPartial: Boolean,
    val relevanceScores: Map<String, Double> = emptyMap(),
    val sourceStates: List<SourceSearchState> = emptyList(),
    val queryProcessing: QueryProcessing? = null
)

enum class SearchPhase { QUEUED, RUNNING }

enum class UnsupportedFilter { MINIMUM_SALARY, EMPLOYMENT_TYPE, WORK_MODE }

sealed interface SearchError {
    data class UnsupportedRemoteFilter(
        val fields: Set<UnsupportedFilter>
    ) : SearchError
    data object MissingAccessToken : SearchError
    data object SessionExpired : SearchError
    data object Unauthorized : SearchError
    data object RateLimited : SearchError
    data object NetworkUnavailable : SearchError
    data object Timeout : SearchError
    data object CacheUnavailable : SearchError
    data class Backend(val safeMessage: String) : SearchError
}

class SearchFailure(val error: SearchError) : java.io.IOException()

sealed interface SearchUpdate {
    data class Progress(
        val searchId: String,
        val phase: SearchPhase,
        val resultsSeen: Int = 0,
        val effectiveJobs: Int = 0,
        val completedSources: Int = 0,
        val totalSources: Int = 0,
        val result: SearchResult? = null,
        val sourceStates: List<SourceSearchState> = emptyList(),
        val queryProcessing: QueryProcessing? = null
    ) : SearchUpdate
    data class Results(val result: SearchResult) : SearchUpdate
    data class Failed(val error: SearchError) : SearchUpdate
}

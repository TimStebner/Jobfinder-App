package de.jobfinder.app.domain

import de.jobfinder.app.core.model.Job
import de.jobfinder.app.core.model.SearchQuery
import de.jobfinder.app.core.model.SearchResult
import de.jobfinder.app.core.model.SearchUpdate
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    suspend fun popularJobs(limit: Int): Result<List<Job>>
    fun search(query: SearchQuery): Flow<SearchUpdate>
    fun loadStoredSearch(searchId: String): Flow<SearchUpdate>
    suspend fun loadPage(searchId: String, page: Int, size: Int): Result<SearchResult>
    suspend fun job(id: String): Result<Job?>
    fun observeCached(ids: Set<String>): Flow<List<Job>>
}

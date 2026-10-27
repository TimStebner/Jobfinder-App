package de.jobfinder.app

import de.jobfinder.app.core.model.Job
import de.jobfinder.app.core.model.SearchPhase
import de.jobfinder.app.core.model.SearchQuery
import de.jobfinder.app.core.model.SearchResult
import de.jobfinder.app.core.model.SearchUpdate
import de.jobfinder.app.domain.JobRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeJobRepository(
    private val simulateDelay: Boolean = true
) : JobRepository {

    private val allFixtureJobs: List<Job> = buildList {
        addAll(PreviewJobData.createFiftySearchResults())
        addAll(PreviewJobData.PopularJobs)
        addAll(PreviewJobData.FavoriteJobs)
        add(PreviewJobData.OnboardingFeaturedJob)
    }.distinctBy { it.id }

    override suspend fun popularJobs(limit: Int): Result<List<Job>> {
        if (simulateDelay) delay(100)
        return Result.success(PreviewJobData.PopularJobs.take(limit))
    }

    override fun search(query: SearchQuery): Flow<SearchUpdate> = flow {
        val searchId = "fake-search-${System.currentTimeMillis()}"
        emit(SearchUpdate.Progress(searchId = searchId, phase = SearchPhase.QUEUED, result = null))

        if (simulateDelay) delay(150)
        emit(SearchUpdate.Progress(searchId = searchId, phase = SearchPhase.RUNNING, result = null))

        if (simulateDelay) delay(250)
        val fiftyJobs = PreviewJobData.createFiftySearchResults()
        val result = SearchResult(
            searchId = searchId,
            jobs = fiftyJobs,
            loadedCount = fiftyJobs.size,
            totalCount = 50,
            hasNextPage = false,
            isPartial = false
        )
        emit(SearchUpdate.Results(result))
    }

    override fun loadStoredSearch(searchId: String): Flow<SearchUpdate> = flow {
        val jobs = PreviewJobData.createFiftySearchResults()
        emit(
            SearchUpdate.Results(
                SearchResult(
                    searchId = searchId,
                    jobs = jobs,
                    loadedCount = jobs.size,
                    totalCount = jobs.size,
                    hasNextPage = false,
                    isPartial = false,
                )
            )
        )
    }

    override suspend fun loadPage(searchId: String, page: Int, size: Int): Result<SearchResult> {
        if (simulateDelay) delay(100)
        val fiftyJobs = PreviewJobData.createFiftySearchResults()
        val fromIndex = (page * size).coerceAtMost(fiftyJobs.size)
        val toIndex = (fromIndex + size).coerceAtMost(fiftyJobs.size)
        val pagedList = fiftyJobs.subList(fromIndex, toIndex)

        return Result.success(
            SearchResult(
                searchId = searchId,
                jobs = pagedList,
                loadedCount = pagedList.size,
                totalCount = fiftyJobs.size,
                hasNextPage = toIndex < fiftyJobs.size,
                isPartial = false
            )
        )
    }

    override suspend fun job(id: String): Result<Job?> {
        if (simulateDelay) delay(50)
        val found = allFixtureJobs.find { it.id == id }
        return Result.success(found)
    }

    override fun observeCached(ids: Set<String>): Flow<List<Job>> = flow {
        val matching = allFixtureJobs.filter { it.id in ids }
        emit(matching)
    }
}

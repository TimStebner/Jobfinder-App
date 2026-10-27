package de.jobfinder.app.feature.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.jobfinder.app.core.model.EmploymentType
import de.jobfinder.app.core.model.Job
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.core.model.RecentSearch
import de.jobfinder.app.core.model.SearchError
import de.jobfinder.app.core.model.SearchFailure
import de.jobfinder.app.core.model.SearchFilters
import de.jobfinder.app.core.model.SearchPhase
import de.jobfinder.app.core.model.SearchQuery
import de.jobfinder.app.core.model.SearchResult
import de.jobfinder.app.core.model.SearchUpdate
import de.jobfinder.app.core.model.SourceSearchState
import de.jobfinder.app.core.model.WorkMode
import de.jobfinder.app.core.model.forActiveSearch
import de.jobfinder.app.domain.JobRepository
import de.jobfinder.app.domain.UserPreferencesRepository
import de.jobfinder.app.domain.toUiModel
import kotlinx.coroutines.Job as CoroutineJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

import de.jobfinder.app.core.model.JobSource
import de.jobfinder.app.core.model.JobKind
import de.jobfinder.app.core.model.WorkingTime
import de.jobfinder.app.core.model.matchesFilter
import de.jobfinder.app.core.model.toComparableAnnualGrossEur

enum class JobSortOrder(val displayName: String) {
    RELEVANCE("Relevanz"),
    SALARY_DESC("Gehalt absteigend"),
    SALARY_ASC("Gehalt aufsteigend"),
    NEWEST_FIRST("Neueste zuerst"),
    OLDEST_FIRST("Älteste zuerst"),
    DISTANCE_ASC("Entfernung aufsteigend"),
    DISTANCE_DESC("Entfernung absteigend")
}

data class ClientJobFilters(
    val minSalary: Double? = null,
    val maxSalary: Double? = null,
    val categories: Set<String> = emptySet(),
    val includeUnclassified: Boolean = false,
    val workingTimes: Set<WorkingTime> = emptySet(),
    val employmentTypes: Set<EmploymentType> = emptySet(),
    val jobKinds: Set<JobKind> = emptySet(),
    val workModes: Set<WorkMode> = emptySet(),
    val sources: Set<JobSource> = emptySet()
) {
    fun isActive(): Boolean =
        minSalary != null ||
            maxSalary != null ||
            categories.isNotEmpty() ||
            includeUnclassified ||
            workingTimes.isNotEmpty() ||
            employmentTypes.isNotEmpty() ||
            jobKinds.isNotEmpty() ||
            workModes.isNotEmpty() ||
            sources.isNotEmpty()
}

data class SearchUiState(
    val query: String = "",
    val queryNotice: String? = null,
    val location: String = "Berlin",
    val filters: SearchFilters = SearchFilters(),
    val clientFilters: ClientJobFilters = ClientJobFilters(),
    val sortOrder: JobSortOrder = JobSortOrder.RELEVANCE,
    val jobs: List<JobUiModel> = emptyList(),
    val currentPage: Int = 1,
    val pageSize: Int = 50,
    val totalPages: Int = 1,
    val totalFilteredCount: Int = 0,
    val loadedCount: Int = 0,
    val totalCount: Int? = null,
    val hasNextPage: Boolean = false,
    val isPartial: Boolean = false,
    val sourceStates: List<SourceSearchState> = emptyList(),
    val isLoadingResults: Boolean = false,
    val isResultSetComplete: Boolean = true,
    val resultLoadError: SearchError? = null,
    val isSearching: Boolean = false,
    val isPolling: Boolean = false,
    val searchPhase: SearchPhase? = null,
    val resultsSeen: Int = 0,
    val effectiveJobs: Int = 0,
    val completedSources: Int = 0,
    val totalSources: Int = 0,
    val error: SearchError? = null,
    val favoriteIds: Set<String> = emptySet()
)

class SearchViewModel(
    private val jobRepository: JobRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var activeSearchJob: CoroutineJob? = null
    private var backgroundPagesJob: CoroutineJob? = null
    private var lastSearchQuery: SearchQuery? = null
    private var rawJobs: List<Job> = emptyList()
    private val relevanceScores = mutableMapOf<String, Double>()
    private var currentSearchId: String? = null
    private var backendPage: Int = 1

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            preferencesRepository.favoriteIds.collect { favorites ->
                _uiState.update { it.copy(favoriteIds = favorites) }
                recomputeJobs()
            }
        }
    }

    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun setLocation(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun applyFilters(filters: SearchFilters) {
        _uiState.update { it.copy(filters = filters) }
    }

    fun submitSearch() {
        val currentState = _uiState.value
        val normalizedFilters = currentState.filters.copy(
            sources = currentState.filters.sources.forActiveSearch()
        )
        val searchQuery = SearchQuery(
            query = currentState.query,
            location = currentState.location,
            filters = normalizedFilters
        )
        lastSearchQuery = searchQuery
        _uiState.update { it.copy(filters = normalizedFilters) }

        runSearch(searchQuery)
    }

    fun openRecentSearch(search: RecentSearch) {
        val searchQuery = SearchQuery(
            query = search.query,
            location = search.location,
            filters = SearchFilters(
                radiusKm = search.radiusKm,
                sources = search.sources.forActiveSearch(),
                minSalary = search.minSalary,
                employmentTypes = search.employmentTypes,
                remoteOnly = search.isRemote,
            ),
        )
        lastSearchQuery = searchQuery
        _uiState.update {
            it.copy(
                query = search.query,
                location = search.location,
                filters = searchQuery.filters,
            )
        }

        val searchId = search.searchId
        if (searchId == null) {
            runSearch(searchQuery)
            return
        }

        activeSearchJob?.cancel()
        prepareSearchState()
        activeSearchJob = viewModelScope.launch {
            var cacheUnavailable = false
            jobRepository.loadStoredSearch(searchId).collect { update ->
                if (update is SearchUpdate.Failed && update.error == SearchError.CacheUnavailable) {
                    cacheUnavailable = true
                } else {
                    handleSearchUpdate(update)
                }
            }
            if (cacheUnavailable) {
                jobRepository.search(searchQuery).collect(::handleSearchUpdate)
            }
        }
    }

    fun retry() {
        val searchId = currentSearchId
        if (_uiState.value.error == SearchError.Timeout && searchId != null) {
            resumeStoredSearch(searchId)
        } else {
            lastSearchQuery?.let(::runSearch)
        }
    }

    private fun resumeStoredSearch(searchId: String) {
        activeSearchJob?.cancel()
        prepareSearchState()
        activeSearchJob = viewModelScope.launch {
            jobRepository.loadStoredSearch(searchId).collect(::handleSearchUpdate)
        }
    }

    private fun runSearch(searchQuery: SearchQuery) {
        activeSearchJob?.cancel()
        prepareSearchState()
        activeSearchJob = viewModelScope.launch {
            jobRepository.search(searchQuery).collect(::handleSearchUpdate)
        }
    }

    private fun prepareSearchState() {
        backgroundPagesJob?.cancel()
        _uiState.update {
            it.copy(
                isSearching = true,
                isPolling = false,
                isPartial = false,
                sourceStates = emptyList(),
                queryNotice = null,
                isLoadingResults = false,
                isResultSetComplete = false,
                resultLoadError = null,
                resultsSeen = 0,
                effectiveJobs = 0,
                completedSources = 0,
                totalSources = 0,
                error = null,
            )
        }
    }

    private fun handleSearchUpdate(update: SearchUpdate) {
        when (update) {
            is SearchUpdate.Progress -> {
                currentSearchId = update.searchId
                _uiState.update {
                    it.copy(
                        isPolling = true,
                        searchPhase = update.phase,
                        resultsSeen = update.resultsSeen,
                        effectiveJobs = update.effectiveJobs,
                        completedSources = update.completedSources,
                        totalSources = update.totalSources,
                        sourceStates = if (update.sourceStates.isNotEmpty()) update.sourceStates else it.sourceStates,
                        queryNotice = if (update.queryProcessing != null)
                            update.queryProcessing.correctionNotice(it.query) else it.queryNotice,
                    )
                }
                if (update.result != null) {
                    applySearchResult(update.result)
                }
            }
            is SearchUpdate.Results -> {
                currentSearchId = update.result.searchId
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        isPolling = false,
                        searchPhase = null,
                        effectiveJobs = update.result.totalCount ?: update.result.loadedCount,
                    )
                }
                applySearchResult(update.result)
                lastSearchQuery?.let { query -> saveRecentSearch(query, update.result.searchId) }
            }
            is SearchUpdate.Failed -> {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        isPolling = false,
                        searchPhase = null,
                        error = update.error,
                    )
                }
            }
        }
    }

    private fun saveRecentSearch(query: SearchQuery, searchId: String) {
        if (query.query.isBlank()) return
        viewModelScope.launch {
            preferencesRepository.saveRecentSearch(
                RecentSearch(
                    id = UUID.randomUUID().toString(),
                    searchId = searchId,
                    query = query.query,
                    location = query.location,
                    radiusKm = query.filters.radiusKm,
                    sources = query.filters.sources,
                    minSalary = query.filters.minSalary,
                    employmentTypes = query.filters.employmentTypes,
                    isRemote = query.filters.remoteOnly,
                )
            )
        }
    }

    private fun applySearchResult(result: SearchResult) {
        currentSearchId = result.searchId
        rawJobs = result.jobs
        backendPage = 1
        relevanceScores.clear()
        relevanceScores.putAll(result.relevanceScores)
        val isComplete = !result.hasNextPage
        _uiState.update {
            it.copy(
                loadedCount = result.loadedCount,
                totalCount = result.totalCount,
                hasNextPage = result.hasNextPage,
                isPartial = result.isPartial,
                sourceStates = if (result.sourceStates.isNotEmpty()) result.sourceStates else it.sourceStates,
                queryNotice = if (result.queryProcessing != null)
                    result.queryProcessing.correctionNotice(it.query) else it.queryNotice,
                isLoadingResults = !isComplete,
                isResultSetComplete = isComplete,
                resultLoadError = null,
                currentPage = 1
            )
        }
        recomputeJobs()

        if (result.hasNextPage) {
            loadRemainingPages(result.searchId, startFromPage = 2)
        }
    }

    fun retryLoadResults() {
        val searchId = currentSearchId ?: return
        if (_uiState.value.resultLoadError != null) {
            loadRemainingPages(searchId, startFromPage = backendPage + 1)
        }
    }

    private fun loadRemainingPages(searchId: String, startFromPage: Int) {
        backgroundPagesJob?.cancel()
        _uiState.update {
            it.copy(
                isLoadingResults = true,
                isResultSetComplete = false,
                resultLoadError = null,
            )
        }
        backgroundPagesJob = viewModelScope.launch {
            var page = startFromPage
            while (coroutineContext.isActive) {
                val result = jobRepository.loadPage(
                    searchId = searchId,
                    page = page,
                    size = _uiState.value.pageSize,
                )

                if (currentSearchId != searchId) return@launch

                if (result.isFailure) {
                    val err = (result.exceptionOrNull() as? SearchFailure)?.error
                        ?: SearchError.Backend("Fehler beim Laden von Seite $page")
                    _uiState.update {
                        it.copy(
                            isLoadingResults = false,
                            isResultSetComplete = false,
                            resultLoadError = err,
                        )
                    }
                    return@launch
                }

                val pageResult = result.getOrThrow()
                val existingIds = rawJobs.asSequence().map(Job::id).toSet()
                val newJobs = pageResult.jobs.filter { it.id !in existingIds }

                if (newJobs.isEmpty() && pageResult.hasNextPage) {
                    _uiState.update {
                        it.copy(
                            isLoadingResults = false,
                            isResultSetComplete = false,
                            resultLoadError = SearchError.Backend("Fehlerhafte Paginierung: Leere Seite mit has_next=true"),
                        )
                    }
                    return@launch
                }

                rawJobs = rawJobs + newJobs
                relevanceScores.putAll(pageResult.relevanceScores)
                backendPage = page

                _uiState.update {
                    it.copy(
                        loadedCount = rawJobs.size,
                        totalCount = pageResult.totalCount ?: it.totalCount,
                        hasNextPage = pageResult.hasNextPage,
                    )
                }
                recomputeJobs()

                page++

                if (!pageResult.hasNextPage) {
                    break
                }
            }

            if (currentSearchId == searchId) {
                _uiState.update {
                    it.copy(
                        isLoadingResults = false,
                        isResultSetComplete = true,
                        resultLoadError = null,
                    )
                }
                recomputeJobs()
            }
        }
    }

    fun applyClientFilters(filters: ClientJobFilters) {
        _uiState.update { it.copy(clientFilters = filters, currentPage = 1) }
        recomputeJobs()
    }

    fun setSortOrder(order: JobSortOrder) {
        _uiState.update { it.copy(sortOrder = order, currentPage = 1) }
        recomputeJobs()
    }

    fun goToPage(page: Int) {
        val state = _uiState.value
        val target = page.coerceIn(1, state.totalPages)
        _uiState.update { it.copy(currentPage = target) }
        recomputeJobs()
    }

    fun nextPage() {
        val state = _uiState.value
        if (state.currentPage < state.totalPages) {
            goToPage(state.currentPage + 1)
        }
    }

    fun previousPage() {
        val current = _uiState.value.currentPage
        if (current > 1) {
            goToPage(current - 1)
        }
    }

    fun loadNextPage() {
        nextPage()
    }

    private fun recomputeJobs() {
        val state = _uiState.value
        val cf = state.clientFilters

        val filtered = rawJobs.filter { job ->
            val matchesSalary = job.salary.matchesFilter(cf.minSalary, cf.maxSalary)

            val matchesCategory = if (cf.categories.isEmpty() && !cf.includeUnclassified) {
                true
            } else {
                val isUnclassified = job.primaryCategory == null || job.classificationVersion == 0
                val matchesUnclassified = cf.includeUnclassified && isUnclassified
                val jobCategories = if (!isUnclassified) {
                    listOfNotNull(job.primaryCategory) + job.secondaryCategories
                } else {
                    emptyList()
                }
                val matchesSelectedCategory = jobCategories.any { it in cf.categories }
                matchesUnclassified || matchesSelectedCategory
            }

            val matchesWorkingTime = cf.workingTimes.isEmpty() || job.workingTimes.any { it in cf.workingTimes }
            val matchesEmp = cf.employmentTypes.isEmpty() || (job.employmentType != null && job.employmentType in cf.employmentTypes)
            val matchesKind = cf.jobKinds.isEmpty() || job.jobKind in cf.jobKinds
            val matchesWork = cf.workModes.isEmpty() || (job.workMode != null && job.workMode in cf.workModes) || job.workModes.any { it in cf.workModes }
            val matchesSource = if (cf.sources.isEmpty()) {
                true
            } else {
                val allSources = (job.sources + listOfNotNull(job.primarySource)).toSet()
                allSources.any { it in cf.sources }
            }

            matchesSalary && matchesCategory && matchesWorkingTime && matchesEmp && matchesKind && matchesWork && matchesSource
        }

        val sorted = when (state.sortOrder) {
            JobSortOrder.RELEVANCE -> filtered.sortedWith { a, b ->
                val scoreA = relevanceScores[a.id]
                val scoreB = relevanceScores[b.id]
                when {
                    scoreA != null && scoreB != null -> {
                        val diff = scoreB.compareTo(scoreA)
                        if (diff != 0) diff else 0
                    }
                    scoreA != null -> -1
                    scoreB != null -> 1
                    else -> 0
                }
            }
            JobSortOrder.SALARY_DESC -> filtered.sortedWith { a, b ->
                val rangeA = a.salary?.toComparableAnnualGrossEur()
                val rangeB = b.salary?.toComparableAnnualGrossEur()
                when {
                    rangeA != null && rangeB != null -> {
                        val cmp = rangeB.second.compareTo(rangeA.second)
                        if (cmp != 0) cmp else rangeB.first.compareTo(rangeA.first)
                    }
                    rangeA != null -> -1
                    rangeB != null -> 1
                    else -> 0
                }
            }
            JobSortOrder.SALARY_ASC -> filtered.sortedWith { a, b ->
                val rangeA = a.salary?.toComparableAnnualGrossEur()
                val rangeB = b.salary?.toComparableAnnualGrossEur()
                when {
                    rangeA != null && rangeB != null -> {
                        val cmp = rangeA.first.compareTo(rangeB.first)
                        if (cmp != 0) cmp else rangeA.second.compareTo(rangeB.second)
                    }
                    rangeA != null -> -1
                    rangeB != null -> 1
                    else -> 0
                }
            }
            JobSortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.postedDateText ?: "" }
            JobSortOrder.OLDEST_FIRST -> filtered.sortedBy { it.postedDateText ?: "" }
            JobSortOrder.DISTANCE_ASC -> filtered.sortedWith(compareBy({ it.distanceKm ?: Double.MAX_VALUE }, { it.id }))
            JobSortOrder.DISTANCE_DESC -> filtered.sortedByDescending { it.distanceKm ?: -1.0 }
        }

        val pageSize = state.pageSize
        val authoritativeCount = if (!state.isResultSetComplete || cf.isActive()) sorted.size else state.totalCount ?: sorted.size
        val totalPages = maxOf(1, (authoritativeCount + pageSize - 1) / pageSize)
        val validPage = state.currentPage.coerceIn(1, totalPages)
        val startIndex = (validPage - 1) * pageSize
        val pageItems = if (startIndex < sorted.size) {
            sorted.subList(startIndex, minOf(startIndex + pageSize, sorted.size))
        } else {
            emptyList()
        }

        val favorites = state.favoriteIds
        _uiState.update {
            it.copy(
                currentPage = validPage,
                totalPages = totalPages,
                totalFilteredCount = authoritativeCount,
                jobs = pageItems.map { job -> job.toUiModel(favorites.contains(job.id)) }
            )
        }
    }

    fun cancelSearch() {
        activeSearchJob?.cancel()
        backgroundPagesJob?.cancel()
        _uiState.update {
            it.copy(
                isSearching = false,
                isPolling = false,
                isLoadingResults = false,
            )
        }
    }

    fun toggleFavorite(jobId: String) {
        viewModelScope.launch {
            preferencesRepository.toggleFavorite(jobId)
        }
    }
}

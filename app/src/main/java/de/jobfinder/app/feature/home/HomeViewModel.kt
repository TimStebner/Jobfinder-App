package de.jobfinder.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.jobfinder.app.core.model.Job
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.core.model.RecentSearch
import de.jobfinder.app.core.model.SearchFilters
import de.jobfinder.app.domain.JobRepository
import de.jobfinder.app.domain.UserPreferencesRepository
import de.jobfinder.app.domain.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val popularJobs: List<JobUiModel> = emptyList(),
    val recentSearches: List<RecentSearch> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val location: String = "Berlin",
    val filters: SearchFilters = SearchFilters(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val jobRepository: JobRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var rawPopularJobs: List<Job> = emptyList()

    init {
        loadPopularJobs()
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                preferencesRepository.recentSearches,
                preferencesRepository.favoriteIds
            ) { recent, favorites ->
                Pair(recent, favorites)
            }.collect { (recent, favorites) ->
                _uiState.update { current ->
                    current.copy(
                        recentSearches = recent,
                        favoriteIds = favorites,
                        popularJobs = rawPopularJobs.map { it.toUiModel(favorites.contains(it.id)) }
                    )
                }
            }
        }
    }

    fun loadPopularJobs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = jobRepository.popularJobs(limit = 10)
            result.fold(
                onSuccess = { jobs ->
                    rawPopularJobs = jobs
                    val favorites = _uiState.value.favoriteIds
                    _uiState.update { current ->
                        current.copy(
                            popularJobs = jobs.map { it.toUiModel(favorites.contains(it.id)) },
                            isLoading = false
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Fehler beim Laden der Jobs"
                        )
                    }
                }
            )
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setLocation(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun applyFilters(filters: SearchFilters, location: String = _uiState.value.location) {
        _uiState.update { it.copy(filters = filters, location = location) }
    }

    fun toggleFavorite(jobId: String) {
        viewModelScope.launch {
            preferencesRepository.toggleFavorite(jobId)
        }
    }

    fun removeRecentSearch(id: String) {
        viewModelScope.launch {
            preferencesRepository.removeRecentSearch(id)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            preferencesRepository.clearRecentSearches()
        }
    }
}

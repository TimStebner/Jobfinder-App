package de.jobfinder.app.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.domain.JobRepository
import de.jobfinder.app.domain.UserPreferencesRepository
import de.jobfinder.app.domain.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteJobs: List<JobUiModel> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FavoritesViewModel(
    private val jobRepository: JobRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            preferencesRepository.favoriteIds.flatMapLatest { ids ->
                jobRepository.observeCached(ids)
            }.collect { jobs ->
                _uiState.update {
                    it.copy(
                        favoriteJobs = jobs.map { job -> job.toUiModel(isFavorite = true) },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleFavorite(jobId: String) {
        viewModelScope.launch {
            preferencesRepository.toggleFavorite(jobId)
        }
    }
}

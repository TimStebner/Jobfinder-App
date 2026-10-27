package de.jobfinder.app.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.jobfinder.app.core.model.Job
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.domain.JobRepository
import de.jobfinder.app.domain.UserPreferencesRepository
import de.jobfinder.app.domain.toUiModel
import kotlinx.coroutines.Job as CoroutineJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JobDetailsUiState(
    val job: JobUiModel? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val error: String? = null
)

class JobDetailsViewModel(
    private val jobRepository: JobRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobDetailsUiState())
    val uiState: StateFlow<JobDetailsUiState> = _uiState.asStateFlow()

    private var activeJobId: String? = null
    private var observeFavoritesJob: CoroutineJob? = null
    private var currentJob: Job? = null

    fun loadJob(id: String) {
        if (activeJobId == id && (_uiState.value.job != null || _uiState.value.isLoading)) return
        activeJobId = id
        observeFavoritesJob?.cancel()

        viewModelScope.launch {
            // The list already cached the job: show it at once, the full details replace it when they arrive.
            val cached = jobRepository.observeCached(setOf(id)).first().firstOrNull()
            _uiState.update {
                it.copy(job = cached?.toUiModel(it.isFavorite) ?: it.job, isLoading = true, error = null)
            }
            val result = jobRepository.job(id)
            result.fold(
                onSuccess = { job ->
                    currentJob = job
                    if (job != null) {
                        observeFavoritesJob = viewModelScope.launch {
                            preferencesRepository.favoriteIds.collect { favorites ->
                                val isFav = favorites.contains(job.id)
                                _uiState.update {
                                    it.copy(
                                        job = job.toUiModel(isFavorite = isFav),
                                        isFavorite = isFav,
                                        isLoading = false,
                                        error = null
                                    )
                                }
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                job = null,
                                isLoading = false,
                                error = "Job nicht gefunden"
                            )
                        }
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            job = null,
                            isLoading = false,
                            error = throwable.message ?: "Fehler beim Laden des Jobs"
                        )
                    }
                }
            )
        }
    }

    fun toggleFavorite() {
        val jobId = currentJob?.id ?: return
        viewModelScope.launch {
            preferencesRepository.toggleFavorite(jobId)
        }
    }
}

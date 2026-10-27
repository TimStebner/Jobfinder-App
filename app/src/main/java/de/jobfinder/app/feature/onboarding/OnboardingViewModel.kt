package de.jobfinder.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.jobfinder.app.core.model.OnboardingPreferences
import de.jobfinder.app.domain.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 2,
    val preferences: OnboardingPreferences = OnboardingPreferences(),
    val isCompleted: Boolean = false
)

class OnboardingViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.onboardingPreferences.collect { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
    }

    fun toggleFullTime() {
        _uiState.update {
            it.copy(preferences = it.preferences.copy(fullTime = !it.preferences.fullTime))
        }
    }

    fun togglePartTime() {
        _uiState.update {
            it.copy(preferences = it.preferences.copy(partTime = !it.preferences.partTime))
        }
    }

    fun toggleRemote() {
        _uiState.update {
            it.copy(preferences = it.preferences.copy(remote = !it.preferences.remote))
        }
    }

    fun nextStep() {
        _uiState.update {
            val next = (it.currentStep + 1).coerceAtMost(it.totalSteps - 1)
            it.copy(currentStep = next)
        }
    }

    fun previousStep() {
        _uiState.update {
            val prev = (it.currentStep - 1).coerceAtLeast(0)
            it.copy(currentStep = prev)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesRepository.saveOnboarding(
                preferences = _uiState.value.preferences,
                completed = true
            )
            _uiState.update { it.copy(isCompleted = true) }
        }
    }

    fun skip() {
        completeOnboarding()
    }
}

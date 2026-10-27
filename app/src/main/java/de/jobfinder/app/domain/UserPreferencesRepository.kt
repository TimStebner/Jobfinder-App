package de.jobfinder.app.domain

import de.jobfinder.app.core.model.OnboardingPreferences
import de.jobfinder.app.core.model.RecentSearch
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val onboardingPreferences: Flow<OnboardingPreferences>
    val onboardingCompleted: Flow<Boolean>
    val recentSearches: Flow<List<RecentSearch>>
    val favoriteIds: Flow<Set<String>>

    suspend fun saveOnboarding(preferences: OnboardingPreferences, completed: Boolean)
    suspend fun saveRecentSearch(search: RecentSearch)
    suspend fun removeRecentSearch(id: String)
    suspend fun clearRecentSearches()
    suspend fun toggleFavorite(jobId: String)
}

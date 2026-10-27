package de.jobfinder.app.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import de.jobfinder.app.core.model.OnboardingPreferences
import de.jobfinder.app.core.model.RecentSearch
import de.jobfinder.app.domain.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataStoreUserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    companion object {
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_PREF_FULL_TIME = booleanPreferencesKey("pref_full_time")
        val KEY_PREF_PART_TIME = booleanPreferencesKey("pref_part_time")
        val KEY_PREF_REMOTE = booleanPreferencesKey("pref_remote")
        val KEY_FAVORITE_IDS = stringSetPreferencesKey("favorite_ids")
        val KEY_RECENT_SEARCHES_JSON = stringPreferencesKey("recent_searches_json")
        const val MAX_RECENT_SEARCHES = 10
    }

    override val onboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    override val onboardingPreferences: Flow<OnboardingPreferences> = dataStore.data.map { preferences ->
        OnboardingPreferences(
            fullTime = preferences[KEY_PREF_FULL_TIME] ?: true,
            partTime = preferences[KEY_PREF_PART_TIME] ?: false,
            remote = preferences[KEY_PREF_REMOTE] ?: false
        )
    }

    override val recentSearches: Flow<List<RecentSearch>> = dataStore.data.map { preferences ->
        val rawJson = preferences[KEY_RECENT_SEARCHES_JSON]
        if (rawJson.isNullOrBlank()) {
            emptyList()
        } else {
            try {
                json.decodeFromString<List<RecentSearch>>(rawJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override val favoriteIds: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[KEY_FAVORITE_IDS] ?: emptySet()
    }

    override suspend fun saveOnboarding(preferences: OnboardingPreferences, completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_PREF_FULL_TIME] = preferences.fullTime
            prefs[KEY_PREF_PART_TIME] = preferences.partTime
            prefs[KEY_PREF_REMOTE] = preferences.remote
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    override suspend fun saveRecentSearch(search: RecentSearch) {
        dataStore.edit { prefs ->
            val rawJson = prefs[KEY_RECENT_SEARCHES_JSON]
            val existingList: List<RecentSearch> = if (rawJson.isNullOrBlank()) {
                emptyList()
            } else {
                try {
                    json.decodeFromString<List<RecentSearch>>(rawJson)
                } catch (e: Exception) {
                    emptyList()
                }
            }

            // Remove existing duplicate by query and location
        val filtered = existingList.filterNot { it.sameRequestAs(search) }

            // Prepend new search and cap to MAX_RECENT_SEARCHES
            val updated = listOf(search) + filtered
            val capped = updated.take(MAX_RECENT_SEARCHES)

            prefs[KEY_RECENT_SEARCHES_JSON] = json.encodeToString(capped)
        }
    }

    private fun RecentSearch.sameRequestAs(other: RecentSearch): Boolean =
        query.equals(other.query, ignoreCase = true) &&
            location.equals(other.location, ignoreCase = true) &&
            radiusKm == other.radiusKm &&
            sources == other.sources &&
            minSalary == other.minSalary &&
            employmentTypes == other.employmentTypes &&
            isRemote == other.isRemote

    override suspend fun removeRecentSearch(id: String) {
        dataStore.edit { prefs ->
            val rawJson = prefs[KEY_RECENT_SEARCHES_JSON] ?: return@edit
            val list = try {
                json.decodeFromString<List<RecentSearch>>(rawJson)
            } catch (e: Exception) {
                emptyList()
            }
            val updated = list.filterNot { it.id == id }
            prefs[KEY_RECENT_SEARCHES_JSON] = json.encodeToString(updated)
        }
    }

    override suspend fun clearRecentSearches() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_RECENT_SEARCHES_JSON)
        }
    }

    override suspend fun toggleFavorite(jobId: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_FAVORITE_IDS]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(jobId)) {
                current.remove(jobId)
            } else {
                current.add(jobId)
            }
            prefs[KEY_FAVORITE_IDS] = current
        }
    }
}

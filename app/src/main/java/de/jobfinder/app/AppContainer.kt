package de.jobfinder.app

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import de.jobfinder.app.data.DataStoreUserPreferencesRepository
import de.jobfinder.app.domain.JobRepository
import de.jobfinder.app.domain.UserPreferencesRepository
import de.jobfinder.app.feature.details.JobDetailsViewModel
import de.jobfinder.app.feature.favorites.FavoritesViewModel
import de.jobfinder.app.feature.home.HomeViewModel
import de.jobfinder.app.feature.jobs.SearchViewModel
import de.jobfinder.app.feature.onboarding.OnboardingViewModel

private val Context.appDataStore by preferencesDataStore(name = "user_preferences")

interface AppContainer {
    val jobRepository: JobRepository
    val userPreferencesRepository: UserPreferencesRepository

    fun createHomeViewModel(): HomeViewModel
    fun createSearchViewModel(): SearchViewModel
    fun createFavoritesViewModel(): FavoritesViewModel
    fun createJobDetailsViewModel(): JobDetailsViewModel
    fun createOnboardingViewModel(): OnboardingViewModel
}

class DefaultAppContainer(
    private val context: Context,
    override val jobRepository: JobRepository = VariantRepositoryFactory.createJobRepository(
        context.applicationContext,
    ),
    override val userPreferencesRepository: UserPreferencesRepository = DataStoreUserPreferencesRepository(context.appDataStore)
) : AppContainer {

    override fun createHomeViewModel(): HomeViewModel =
        HomeViewModel(jobRepository, userPreferencesRepository)

    override fun createSearchViewModel(): SearchViewModel =
        SearchViewModel(jobRepository, userPreferencesRepository)

    override fun createFavoritesViewModel(): FavoritesViewModel =
        FavoritesViewModel(jobRepository, userPreferencesRepository)

    override fun createJobDetailsViewModel(): JobDetailsViewModel =
        JobDetailsViewModel(jobRepository, userPreferencesRepository)

    override fun createOnboardingViewModel(): OnboardingViewModel =
        OnboardingViewModel(userPreferencesRepository)
}

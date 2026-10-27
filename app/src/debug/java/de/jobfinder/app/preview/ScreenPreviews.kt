package de.jobfinder.app.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.jobfinder.app.PreviewJobData
import de.jobfinder.app.core.designsystem.JobFinderTheme
import de.jobfinder.app.core.model.JobSource
import de.jobfinder.app.core.model.OnboardingPreferences
import de.jobfinder.app.core.model.RecentSearch
import de.jobfinder.app.core.model.SearchFilters
import de.jobfinder.app.domain.toUiModel
import de.jobfinder.app.feature.details.JobDetailsContent
import de.jobfinder.app.feature.favorites.FavoritesScreenContent
import de.jobfinder.app.feature.home.HomeFilterBottomSheet
import de.jobfinder.app.feature.home.HomeScreenContent
import de.jobfinder.app.feature.home.HomeUiState
import de.jobfinder.app.feature.jobs.JobsScreenContent
import de.jobfinder.app.feature.jobs.SearchUiState
import de.jobfinder.app.feature.onboarding.OnboardingStepOne
import de.jobfinder.app.feature.onboarding.OnboardingStepTwo

@Preview(name = "Onboarding Step 1", showBackground = true, widthDp = 390, heightDp = 884)
@Composable
fun OnboardingStep1Preview() {
    JobFinderTheme {
        OnboardingStepOne(
            onNext = {},
            onSkip = {}
        )
    }
}

@Preview(name = "Onboarding Step 2", showBackground = true, widthDp = 333, heightDp = 775)
@Composable
fun OnboardingStep2Preview() {
    JobFinderTheme {
        OnboardingStepTwo(
            preferences = OnboardingPreferences(fullTime = true, partTime = false, remote = false),
            onToggleFullTime = {},
            onTogglePartTime = {},
            onToggleRemote = {},
            onBack = {},
            onComplete = {},
            onSkip = {}
        )
    }
}

@Preview(name = "Home Screen", showBackground = true, widthDp = 335, heightDp = 772)
@Composable
fun HomePreview() {
    JobFinderTheme {
        HomeScreenContent(
            state = HomeUiState(
                popularJobs = PreviewJobData.PopularJobs.map { it.toUiModel(isFavorite = false) },
                recentSearches = listOf(
                    RecentSearch(id = "1", query = "Frontend Developer", location = "Berlin", radiusKm = 25),
                    RecentSearch(id = "2", query = "UX Designer", location = "München", radiusKm = 25, isRemote = true)
                ),
                searchQuery = ""
            ),
            onQueryChange = {},
            onSearch = {},
            onNavigateToJobDetails = {},
            onToggleFavorite = {},
            onRemoveRecentSearch = {},
            onClearRecentSearches = {},
            onApplyFilters = {}
        )
    }
}

@Preview(name = "Home Filter Sheet", showBackground = true, widthDp = 335, heightDp = 772)
@Composable
fun HomeFilterPreview() {
    JobFinderTheme {
        HomeFilterBottomSheet(
            initialFilters = SearchFilters(
                radiusKm = 25,
                sources = JobSource.activeSearchSources
            ),
            onApply = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Jobs Screen", showBackground = true, widthDp = 353, heightDp = 800)
@Composable
fun JobsPreview() {
    JobFinderTheme {
        JobsScreenContent(
            state = SearchUiState(
                query = "Fachinformatiker",
                location = "Siegburg",
                jobs = PreviewJobData.createFiftySearchResults().map { it.toUiModel(isFavorite = false) },
                loadedCount = 50,
                totalCount = 50,
                filters = SearchFilters(radiusKm = 25)
            ),
            onJobClick = {},
            onToggleFavorite = {},
            onApplyFilters = {},
            onLoadNextPage = {}
        )
    }
}

@Preview(name = "Jobs Filter Sheet", showBackground = true, widthDp = 353, heightDp = 800)
@Composable
fun JobsFilterPreview() {
    JobFinderTheme {
        HomeFilterBottomSheet(
            initialFilters = SearchFilters(
                radiusKm = 25,
                sources = setOf(JobSource.STEPSTONE, JobSource.ARBEITSAGENTUR, JobSource.KLEINANZEIGEN)
            ),
            onApply = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Favorites Screen", showBackground = true, widthDp = 353, heightDp = 800)
@Composable
fun FavoritesPreview() {
    JobFinderTheme {
        FavoritesScreenContent(
            favoriteJobs = PreviewJobData.FavoriteJobs.map { it.toUiModel(isFavorite = true) },
            isLoading = false,
            onJobClick = {},
            onToggleFavorite = {},
            onExploreJobs = {}
        )
    }
}

@Preview(name = "Job Details Screen", showBackground = true, widthDp = 390, heightDp = 884)
@Composable
fun JobDetailsPreview() {
    JobFinderTheme {
        JobDetailsContent(
            job = PreviewJobData.DetailJob.toUiModel(isFavorite = true),
            isFavorite = true,
            onBackClick = {},
            onToggleFavorite = {},
            onApplyClick = {}
        )
    }
}


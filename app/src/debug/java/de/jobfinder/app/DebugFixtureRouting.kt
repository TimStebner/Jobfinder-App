package de.jobfinder.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.jobfinder.app.core.designsystem.JobFinderTheme
import de.jobfinder.app.core.designsystem.components.AppNavItem
import de.jobfinder.app.core.designsystem.components.FloatingBottomNavigation
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

object DebugFixtureRouting {
    @Composable
    fun renderFixture(fixtureId: String): Boolean {
        val navItems = listOf(
            AppNavItem.Home,
            AppNavItem.Jobs,
            AppNavItem.Favorites
        )

        when (fixtureId) {
            "onboarding-1" -> {
                JobFinderTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFAF9FD))
                    ) {
                        OnboardingStepOne(
                            onNext = {},
                            onSkip = {}
                        )
                    }
                }
                return true
            }
            "onboarding-2" -> {
                JobFinderTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFAF9FD))
                    ) {
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
                return true
            }
            "home" -> {
                JobFinderTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
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
                        FloatingBottomNavigation(
                            items = navItems,
                            currentDestination = AppNavItem.Home,
                            onNavigate = {},
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                        )
                    }
                }
                return true
            }
            "home-filter" -> {
                JobFinderTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
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
                return true
            }
            "jobs" -> {
                JobFinderTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
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
                        FloatingBottomNavigation(
                            items = navItems,
                            currentDestination = AppNavItem.Jobs,
                            onNavigate = {},
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                        )
                    }
                }
                return true
            }
            "jobs-filter" -> {
                JobFinderTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
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
                return true
            }
            "favorites" -> {
                JobFinderTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        FavoritesScreenContent(
                            favoriteJobs = PreviewJobData.FavoriteJobs.map { it.toUiModel(isFavorite = true) },
                            isLoading = false,
                            onJobClick = {},
                            onToggleFavorite = {},
                            onExploreJobs = {}
                        )
                        FloatingBottomNavigation(
                            items = navItems,
                            currentDestination = AppNavItem.Favorites,
                            onNavigate = {},
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                        )
                    }
                }
                return true
            }
            "job-details" -> {
                JobFinderTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        JobDetailsContent(
                            job = PreviewJobData.DetailJob.toUiModel(isFavorite = true),
                            isFavorite = true,
                            onBackClick = {},
                            onToggleFavorite = {},
                            onApplyClick = {}
                        )
                    }
                }
                return true
            }
            else -> return false
        }
    }
}

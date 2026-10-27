package de.jobfinder.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import de.jobfinder.app.AppContainer
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.feature.details.JobDetailsScreen
import de.jobfinder.app.feature.favorites.FavoritesScreen
import de.jobfinder.app.feature.home.HomeScreen
import de.jobfinder.app.feature.jobs.JobsScreen
import de.jobfinder.app.feature.onboarding.OnboardingScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JobFinderNavHost(
    navController: NavHostController,
    container: AppContainer,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    val searchViewModel = viewModel { container.createSearchViewModel() }
    // Single-top so a double tap during the transition cannot stack a second details screen.
    val openJob: (String) -> Unit = { jobId ->
        navController.navigate(Destination.JobDetails.createRoute(jobId)) { launchSingleTop = true }
    }
    val openJobsTab: () -> Unit = {
        navController.navigateToTab(Destination.Jobs.route)
    }

    // The page on top carries the whole transition, the one underneath stays opaque (see navEnter): a pushed page
    // is on top while it enters, a popped one while it leaves.
    SharedTransitionLayout(modifier = modifier) {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { navEnter() },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { popExit() }
            ) {
                composable(Destination.Onboarding.route) {
                    val onboardingViewModel = viewModel { container.createOnboardingViewModel() }
                    OnboardingScreen(
                        viewModel = onboardingViewModel,
                        onOnboardingComplete = {
                            navController.navigate(Destination.Home.route) {
                                popUpTo(Destination.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                screen(Destination.Home.route) {
                    val homeViewModel = viewModel { container.createHomeViewModel() }
                    val searchState by searchViewModel.uiState.collectAsStateWithLifecycle()
                    HomeScreen(
                        viewModel = homeViewModel,
                        activeSearchState = searchState,
                        onNavigateToActiveSearch = openJobsTab,
                        onNavigateToRecentSearch = { search ->
                            searchViewModel.openRecentSearch(search)
                            openJobsTab()
                        },
                        onNavigateToSearch = { query, location, filters ->
                            searchViewModel.setQuery(query)
                            searchViewModel.setLocation(location)
                            searchViewModel.applyFilters(filters)
                            searchViewModel.submitSearch()
                            openJobsTab()
                        },
                        onNavigateToJobDetails = openJob
                    )
                }

                screen(Destination.Jobs.route) {
                    JobsScreen(
                        viewModel = searchViewModel,
                        onNavigateToJobDetails = openJob
                    )
                }

                screen(Destination.Favorites.route) {
                    val favoritesViewModel = viewModel { container.createFavoritesViewModel() }
                    FavoritesScreen(
                        viewModel = favoritesViewModel,
                        onNavigateToJobDetails = openJob,
                        onNavigateToSearch = openJobsTab
                    )
                }

                screen(
                    route = Destination.JobDetails.route,
                    arguments = listOf(
                        navArgument(Destination.JobDetails.ARG_JOB_ID) { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val jobId = backStackEntry.arguments?.getString(Destination.JobDetails.ARG_JOB_ID).orEmpty()
                    // Loaded on creation, not from an effect after the first frame: the container grows with content.
                    val detailsViewModel = viewModel(key = jobId) {
                        container.createJobDetailsViewModel().also { it.loadJob(jobId) }
                    }
                    // Captured once: the back stack changes during the pop, the shared key must not.
                    val origin = remember(backStackEntry) { navController.previousBackStackEntry?.destination?.route }
                    JobDetailsScreen(
                        jobId = jobId,
                        viewModel = detailsViewModel,
                        onBackClick = { navController.popBackStackIfResumed() },
                        modifier = Modifier.jobSharedBounds(jobId, origin, isDetails = true)
                    )
                }
            }
        }
    }
}

/** A destination whose content can take part in shared transitions (see [jobSharedBounds]). */
private fun NavGraphBuilder.screen(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route, arguments) { entry ->
        CompositionLocalProvider(
            LocalDestinationScope provides this,
            LocalDestinationRoute provides entry.destination.route
        ) { content(entry) }
    }
}

/** Ignores a back tap while the screen is still transitioning, so two taps cannot pop two entries. */
private fun NavController.popBackStackIfResumed() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) popBackStack()
}

/**
 * The new page fades in over the old one, which stays opaque underneath. Fading both (out first, in after a delay)
 * leaves a gap in which only the window background is visible.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.navEnter(): EnterTransition {
    val to = targetState.destination.route
    val shift = tabShift(initialState.destination.route, to)
    return when {
        shift != 0 -> fadeIn(JobFinderMotion.effects()) +
            slideInHorizontally(tween(JobFinderMotion.MediumMs, easing = JobFinderMotion.EmphasizedDecelerate)) {
                it * shift / 10
            }
        // The container transform needs the surface underneath right away.
        to == Destination.JobDetails.route -> fadeIn(tween(JobFinderMotion.ShortMs))
        else -> fadeIn(tween(JobFinderMotion.MediumMs))
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.leavesDetails(): Boolean =
    initialState.destination.route == Destination.JobDetails.route

/**
 * Back is the mirror image: the page being left is on top and fades out, the one underneath stays opaque.
 * From details the shared container carries the back gesture while its card exists; the scale-down only runs
 * without a matching card (paged or filtered away) and gives the predictive-back gesture something to follow.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.popExit(): ExitTransition {
    if (leavesDetails()) return scaleOut(targetScale = 0.9f) + fadeOut(tween(JobFinderMotion.ReturnMs))
    val shift = tabShift(initialState.destination.route, targetState.destination.route)
    val fade = fadeOut(JobFinderMotion.effects())
    return if (shift == 0) fade else fade +
        slideOutHorizontally(tween(JobFinderMotion.ShortMs, easing = JobFinderMotion.EmphasizedAccelerate)) {
            -it * shift / 10
        }
}

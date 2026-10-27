package de.jobfinder.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.designsystem.JobFinderTheme
import de.jobfinder.app.core.designsystem.components.AppNavItem
import de.jobfinder.app.core.designsystem.components.FloatingBottomNavigation
import de.jobfinder.app.navigation.Destination
import de.jobfinder.app.navigation.JobFinderNavHost
import de.jobfinder.app.navigation.TopLevelRoutes
import de.jobfinder.app.navigation.navItemForRoute
import de.jobfinder.app.navigation.navigateToTab
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun JobFinderApp(
    container: AppContainer,
    modifier: Modifier = Modifier,
    fixtureId: String? = null
) {
    if (fixtureId != null) {
        val handled = DebugFixtureRouting.renderFixture(fixtureId = fixtureId)
        if (handled) return
    }

    val navController = rememberNavController()
    val onboardingCompleted by container.userPreferencesRepository.onboardingCompleted
        .collectAsStateWithLifecycle(initialValue = null)

    // Wait until onboarding state is resolved
    if (onboardingCompleted == null) {
        return
    }

    val startDestination = if (onboardingCompleted == true) {
        Destination.Home.route
    } else {
        Destination.Onboarding.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelDestination = currentRoute in TopLevelRoutes

    // Details opened from a tab keep that tab selected while the pill slides out.
    val navRoute = if (currentRoute in TopLevelRoutes) {
        currentRoute
    } else {
        navController.previousBackStackEntry?.destination?.route
    }
    val currentNavItem = navItemForRoute(navRoute)

    val navItems = listOf(
        AppNavItem.Home,
        AppNavItem.Jobs,
        AppNavItem.Favorites
    )

    fun navigateToTopLevel(item: AppNavItem) {
        val targetRoute = when (item) {
            AppNavItem.Home -> Destination.Home.route
            AppNavItem.Jobs -> Destination.Jobs.route
            AppNavItem.Favorites -> Destination.Favorites.route
        }
        navController.navigateToTab(targetRoute)
    }

    JobFinderTheme {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isCompact = maxWidth < 600.dp

            if (isCompact) {
                val navHazeState = rememberHazeState()

                // Compact Screen Layout: Floating bottom navigation
                Box(modifier = Modifier.fillMaxSize()) {
                    JobFinderNavHost(
                        navController = navController,
                        container = container,
                        startDestination = startDestination,
                        modifier = Modifier
                            .fillMaxSize()
                            .hazeSource(navHazeState)
                    )

                    // The pill leaves downwards when a details screen opens and returns with a spring.
                    AnimatedVisibility(
                        visible = isTopLevelDestination,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding(),
                        enter = slideInVertically(JobFinderMotion.spatial()) { it } +
                            fadeIn(JobFinderMotion.effects()),
                        exit = slideOutVertically(JobFinderMotion.effects(JobFinderMotion.MediumMs)) { it } +
                            fadeOut(JobFinderMotion.effects())
                    ) {
                        FloatingBottomNavigation(
                            items = navItems,
                            currentDestination = currentNavItem,
                            onNavigate = { navigateToTopLevel(it) },
                            hazeState = navHazeState
                        )
                    }
                }
            } else {
                // Medium / Expanded Screen Layout: Persistent Navigation Rail
                Row(modifier = Modifier.fillMaxSize()) {
                    if (isTopLevelDestination) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            navItems.forEach { item ->
                                val selected = item == currentNavItem
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTopLevel(item) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = item.iconRes),
                                            contentDescription = item.label,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text(text = item.label) }
                                )
                            }
                        }
                    }

                    JobFinderNavHost(
                        navController = navController,
                        container = container,
                        startDestination = startDestination,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

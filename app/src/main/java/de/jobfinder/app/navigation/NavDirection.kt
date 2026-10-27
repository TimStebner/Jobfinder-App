package de.jobfinder.app.navigation

import androidx.navigation.NavController
import de.jobfinder.app.core.designsystem.components.AppNavItem
import kotlin.math.sign

/** Bottom-navigation tabs, left to right. */
internal val TopLevelRoutes = listOf(Destination.Home.route, Destination.Jobs.route, Destination.Favorites.route)

/**
 * Switches to a tab and saves the stack it leaves. Every tab jump goes through here: a plain navigate() would
 * stack a tab on top of another tab's saved stack. Home is the root, not the graph start (onboarding on first run).
 */
internal fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Destination.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = route != Destination.Home.route
    }
}

/** The tab a route belongs to; routes outside the tab bar (details, onboarding) map to Home. */
internal fun navItemForRoute(route: String?): AppNavItem = when (route) {
    Destination.Jobs.route -> AppNavItem.Jobs
    Destination.Favorites.route -> AppNavItem.Favorites
    else -> AppNavItem.Home
}

/** +1 when moving to a tab on the right of [from], -1 to the left; 0 if either route is not a tab or both are the same. */
internal fun tabShift(from: String?, to: String?): Int {
    val fromIndex = TopLevelRoutes.indexOf(from)
    val toIndex = TopLevelRoutes.indexOf(to)
    return if (fromIndex < 0 || toIndex < 0) 0 else (toIndex - fromIndex).sign
}

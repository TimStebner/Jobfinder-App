package de.jobfinder.app.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.jobfinder.app.PreviewJobData
import de.jobfinder.app.core.designsystem.JobFinderTheme
import de.jobfinder.app.core.designsystem.components.AppNavItem
import de.jobfinder.app.core.designsystem.components.FeaturedJobCard
import de.jobfinder.app.core.designsystem.components.FilterBottomSheet
import de.jobfinder.app.core.designsystem.components.FloatingBottomNavigation
import de.jobfinder.app.core.designsystem.components.JobDetailHero
import de.jobfinder.app.core.designsystem.components.JobFinderTopBar
import de.jobfinder.app.core.designsystem.components.OnboardingIllustrationStep1
import de.jobfinder.app.core.designsystem.components.OnboardingStepIndicator
import de.jobfinder.app.core.designsystem.components.PreferenceSelectCard
import de.jobfinder.app.core.designsystem.components.PrimaryPillButton
import de.jobfinder.app.core.designsystem.components.SearchField
import de.jobfinder.app.core.designsystem.components.StandardJobCard
import de.jobfinder.app.core.model.SearchFilters
import de.jobfinder.app.domain.toUiModel

@Preview(showBackground = true)
@Composable
fun StandardJobCardPreview() {
    JobFinderTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            StandardJobCard(
                job = PreviewJobData.DetailJob.toUiModel(isFavorite = false),
                onClick = {},
                onToggleFavorite = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeaturedJobCardPreview() {
    JobFinderTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FeaturedJobCard(
                job = PreviewJobData.DetailJob.toUiModel(isFavorite = true),
                onClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun FilterBottomSheetPreview() {
    JobFinderTheme {
        FilterBottomSheet(
            initialFilters = SearchFilters(radiusKm = 25),
            totalResultCount = 50,
            onApply = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingBottomNavigationPreview() {
    JobFinderTheme {
        FloatingBottomNavigation(
            items = listOf(
                AppNavItem.Home,
                AppNavItem.Jobs,
                AppNavItem.Favorites
            ),
            currentDestination = AppNavItem.Home,
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JobDetailHeroPreview() {
    JobFinderTheme {
        JobDetailHero(
            job = PreviewJobData.DetailJob.toUiModel(isFavorite = true),
            onBackClick = {},
            onToggleFavorite = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingIllustrationsPreview() {
    JobFinderTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OnboardingStepIndicator(currentStep = 0, totalSteps = 2)
            OnboardingIllustrationStep1()
            PreferenceSelectCard(
                title = "Vollzeit",
                subtitle = "38 - 40 Stunden pro Woche",
                selected = true,
                onToggle = {}
            )
        }
    }
}

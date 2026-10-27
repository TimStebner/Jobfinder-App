package de.jobfinder.app.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** Light only: `DarkColorScheme` stays in Color.kt, unused until a dark theme is designed. */
@Composable
fun JobFinderTheme(
    content: @Composable () -> Unit
) {
    // Dynamic color is explicitly disabled to preserve the calibrated brand design
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = JobFinderTypography,
        shapes = JobFinderShapesM3,
        content = content
    )
}

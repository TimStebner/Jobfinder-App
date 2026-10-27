package de.jobfinder.app.core.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object JobFinderColors {
    // Brand and component tokens
    val HomeBlue = Color(0xFF285FCB)
    val HeaderBlue = Color(0xFF1D4ED8)
    val ActionBlue = Color(0xFF2563EB)
    val Coral = Color(0xFFE36465)
    val Amber = Color(0xFFF59E0B)
    val Lavender = Color(0xFFF1F0FF)
    val LavenderDark = Color(0xFF24243A)
    val RecentAmber = Color(0xFFF6D886)

    // Three-stop detail gradient: #174A9C -> #1D4ED8 -> #2563EB
    val DetailGradientStops = listOf(
        Color(0xFF174A9C),
        Color(0xFF1D4ED8),
        Color(0xFF2563EB)
    )

    val DetailGradient = Brush.verticalGradient(DetailGradientStops)
    val HomeHeroGradient = Brush.verticalGradient(
        listOf(
            HeaderBlue,
            HomeBlue
        )
    )

    // Semantic tokens from DESIGN.md
    val SurfaceLight = Color(0xFFFBF8FC)
    val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
    val SurfaceContainerLowLight = Color(0xFFF7F4F9)
    val SurfaceContainerLight = Color(0xFFF1EFF4)
    val SurfaceContainerHighLight = Color(0xFFEBE9EE)
    val SurfaceContainerHighestLight = Color(0xFFE5E2E8)
    val OnSurfaceLight = Color(0xFF1B1B20)
    val OnSurfaceVariantLight = Color(0xFF5D626C)
    val OutlineLight = Color(0xFF777680)
    val OutlineVariantLight = Color(0xFFC8C5CE)

    val PrimaryLight = Color(0xFF285FCB)
    val OnPrimaryLight = Color(0xFFFFFFFF)
    val PrimaryContainerLight = Color(0xFFDCE7FF)
    val OnPrimaryContainerLight = Color(0xFF0A275D)

    val SecondaryLight = Color(0xFFB9404B)
    val OnSecondaryLight = Color(0xFFFFFFFF)
    val SecondaryContainerLight = Color(0xFFFFDAD9)
    val OnSecondaryContainerLight = Color(0xFF3F0010)

    val TertiaryLight = Color(0xFF735C00)
    val OnTertiaryLight = Color(0xFFFFFFFF)
    val TertiaryContainerLight = Color(0xFFFFE17B)
    val OnTertiaryContainerLight = Color(0xFF241A00)

    val BackgroundLight = Color(0xFFFBF8FC)
    val OnBackgroundLight = Color(0xFF1B1B1E)

    // Dark semantic tokens
    val SurfaceDark = Color(0xFF111318)
    val SurfaceContainerDark = Color(0xFF1D1F24)
    val OnSurfaceDark = Color(0xFFE4E2E8)
    val OnSurfaceVariantDark = Color(0xFFC4C6D0)
    val OutlineDark = Color(0xFF8E9099)
    val PrimaryDark = Color(0xFFADC6FF)
    val OnPrimaryDark = Color(0xFF002E69)
    val BackgroundDark = Color(0xFF111318)
    val OnBackgroundDark = Color(0xFFE4E2E8)

    // Transparent overlays
    val Scrim40 = Color(0x66000000)
    val White95 = Color(0xF2FFFFFF)
    val White80 = Color(0xCCFFFFFF)
    val White60 = Color(0x99FFFFFF)
}

val LightColorScheme = lightColorScheme(
    primary = JobFinderColors.PrimaryLight,
    onPrimary = JobFinderColors.OnPrimaryLight,
    primaryContainer = JobFinderColors.PrimaryContainerLight,
    onPrimaryContainer = JobFinderColors.OnPrimaryContainerLight,
    secondary = JobFinderColors.SecondaryLight,
    onSecondary = JobFinderColors.OnSecondaryLight,
    secondaryContainer = JobFinderColors.SecondaryContainerLight,
    onSecondaryContainer = JobFinderColors.OnSecondaryContainerLight,
    tertiary = JobFinderColors.TertiaryLight,
    onTertiary = JobFinderColors.OnTertiaryLight,
    tertiaryContainer = JobFinderColors.TertiaryContainerLight,
    onTertiaryContainer = JobFinderColors.OnTertiaryContainerLight,
    background = JobFinderColors.BackgroundLight,
    onBackground = JobFinderColors.OnBackgroundLight,
    surface = JobFinderColors.SurfaceLight,
    onSurface = JobFinderColors.OnSurfaceLight,
    surfaceVariant = JobFinderColors.SurfaceContainerHighestLight,
    onSurfaceVariant = JobFinderColors.OnSurfaceVariantLight,
    outline = JobFinderColors.OutlineLight,
    outlineVariant = JobFinderColors.OutlineVariantLight
)

val DarkColorScheme = darkColorScheme(
    primary = JobFinderColors.PrimaryDark,
    onPrimary = JobFinderColors.OnPrimaryDark,
    background = JobFinderColors.BackgroundDark,
    onBackground = JobFinderColors.OnBackgroundDark,
    surface = JobFinderColors.SurfaceDark,
    onSurface = JobFinderColors.OnSurfaceDark,
    surfaceVariant = JobFinderColors.SurfaceContainerDark,
    onSurfaceVariant = JobFinderColors.OnSurfaceVariantDark,
    outline = JobFinderColors.OutlineDark
)

package de.jobfinder.app.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object JobFinderShapes {
    val CardRadius = 26.dp
    val MetadataPanelRadius = 20.dp
    val FilterSheetTopRadius = 36.dp
    val DetailHeroBottomRadius = 42.dp
    val HomeHeroBottomRadius = 36.dp
    val PillRadius = 999.dp

    val Card = RoundedCornerShape(CardRadius)
    val MetadataPanel = RoundedCornerShape(MetadataPanelRadius)
    val FilterSheet = RoundedCornerShape(topStart = FilterSheetTopRadius, topEnd = FilterSheetTopRadius)
    val DetailHero = RoundedCornerShape(bottomStart = DetailHeroBottomRadius, bottomEnd = DetailHeroBottomRadius)
    val HomeHero = RoundedCornerShape(bottomStart = HomeHeroBottomRadius, bottomEnd = HomeHeroBottomRadius)
    val Pill = RoundedCornerShape(PillRadius)
    val SearchCard = RoundedCornerShape(24.dp)
}

val JobFinderShapesM3 = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

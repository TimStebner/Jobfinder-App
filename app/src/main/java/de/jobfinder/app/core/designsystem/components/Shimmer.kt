package de.jobfinder.app.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.designsystem.rememberAnimationsEnabled

/**
 * Loading placeholder tone with a light band sweeping across. The progress is read in the draw
 * phase only, so the loop never recomposes. With animations off the placeholder stays a flat tone.
 */
@Composable
fun Modifier.shimmer(enabled: Boolean = rememberAnimationsEnabled()): Modifier {
    val base = MaterialTheme.colorScheme.surfaceContainer
    val highlight = MaterialTheme.colorScheme.surfaceContainerLowest
    val progress = if (enabled) {
        rememberInfiniteTransition(label = "shimmer").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(JobFinderMotion.ShimmerMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_progress"
        )
    } else {
        null
    }
    return drawBehind {
        val brush = if (progress == null) {
            SolidColor(base)
        } else {
            // The band starts fully left of the box and ends fully right of it.
            val start = size.width * (2f * progress.value - 1f)
            Brush.linearGradient(
                colors = listOf(base, highlight, base),
                start = Offset(start, 0f),
                end = Offset(start + size.width, 0f)
            )
        }
        drawRect(brush)
    }
}

/** Rounded skeleton block; callers set the size. */
@Composable
fun SkeletonBlock(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(10.dp)).shimmer())
}

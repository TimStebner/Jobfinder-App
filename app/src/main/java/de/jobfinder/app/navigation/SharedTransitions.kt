package de.jobfinder.app.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.jobfinder.app.core.designsystem.JobFinderMotion

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }
val LocalDestinationScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/** Route of the destination a card lives in; scopes the shared key so tabs never pair their cards. */
val LocalDestinationRoute = compositionLocalOf<String?> { null }

internal fun jobSharedKey(origin: String?, jobId: String): String = "job-$origin-$jobId"

/**
 * Container transform between a job card and its details screen, keyed by [jobId] and the screen the
 * card lives in ([origin], defaults to the current destination). Details pass the screen they were
 * opened from. Without the origin, the same job on Home and Jobs would morph during a tab switch.
 * No-op without both scopes (previews, component tests), so cards stay usable standalone.
 *
 * The details screen ([isDetails]) is the container: it stays opaque and is cropped to the animated bounds, never
 * scaled, so no miniature of the white page shows. The card dissolves over it on the way out and back in on the way
 * back. Fading the container itself would show the list through it, and the old and new screen would overlap.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.jobSharedBounds(jobId: String, origin: String? = null, isDetails: Boolean = false): Modifier = composed {
    val sharedScope = LocalSharedTransitionScope.current
    val visibilityScope = LocalDestinationScope.current
    val route = origin ?: LocalDestinationRoute.current
    if (sharedScope == null || visibilityScope == null) {
        Modifier
    } else {
        with(sharedScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = jobSharedKey(route, jobId)),
                animatedVisibilityScope = visibilityScope,
                enter = if (isDetails) EnterTransition.None else fadeIn(JobFinderMotion.effects()),
                exit = if (isDetails) ExitTransition.None else fadeOut(JobFinderMotion.effects()),
                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(
                    ContentScale.None,
                    if (isDetails) Alignment.TopCenter else Alignment.TopStart
                ),
                zIndexInOverlay = if (isDetails) 0f else 1f,
                // Same clock as the nav fades; a spring would still be growing after the list behind it is gone.
                // Shrinking back is quicker than growing.
                boundsTransform = { from, to ->
                    val ms = if (to.height < from.height) JobFinderMotion.ReturnMs else JobFinderMotion.MediumMs
                    tween(ms, easing = JobFinderMotion.EmphasizedDecelerate)
                },
                // Cards are rounded; keep the corners while the container grows.
                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(24.dp))
            )
        }
    }
}

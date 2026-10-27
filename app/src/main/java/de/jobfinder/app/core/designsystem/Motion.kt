package de.jobfinder.app.core.designsystem

import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Single source of motion values. Compose scales every animation by the system animator duration
 * scale, so "Remove animations" yields instant cuts without extra handling.
 */
object JobFinderMotion {
    const val ShortMs = 150
    const val MediumMs = 300
    /** Leaving is quicker than arriving: the details container shrinking back onto its card, and the page it leaves. */
    const val ReturnMs = 200
    const val ShimmerMs = 1200

    // Press-squash targets: the bigger the surface, the subtler the squash.
    const val PressButton = 0.98f
    const val PressCard = 0.985f
    const val PressChip = 0.96f
    const val PressTab = 0.94f
    const val BookmarkPop = 1.18f

    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    /** Position, size and shape changes. */
    fun <T> spatial(): SpringSpec<T> =
        spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)

    /** Color and alpha changes. */
    fun <T> effects(durationMs: Int = ShortMs): TweenSpec<T> =
        tween(durationMs, easing = FastOutSlowInEasing)
}

/** Spring-driven press scale; read the value inside `graphicsLayer {}` to avoid recomposition per frame. */
@Composable
fun InteractionSource.collectPressScale(pressed: Float = 0.97f): State<Float> {
    val isPressed by collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (isPressed) pressed else 1f,
        animationSpec = JobFinderMotion.spatial(),
        label = "press_scale"
    )
}

/**
 * False when the user turned animations off. Compose scales finite animations by the animator duration
 * scale on its own, but endless loops (shimmer, floating) must check this. Read once per composition.
 */
@Composable
fun rememberAnimationsEnabled(): Boolean {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        try {
            Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f
        } catch (_: Exception) {
            true
        }
    }
}

package de.jobfinder.app.core.designsystem

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * Status bar icon tint of the screen on top: white while a blue header covers the bar, dark over light content.
 * Applies once the destination is RESUMED, so a cancelled back gesture (which already composes the screen
 * underneath) never leaves the icons of the wrong screen behind.
 */
@Composable
fun StatusBarIcons(whiteIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    LifecycleResumeEffect(whiteIcons) {
        view.context.findWindow()?.let {
            WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !whiteIcons
        }
        onPauseOrDispose {}
    }
}

/**
 * True while the blue header at the top of a scrolling list still covers the status bar, false once list content
 * passes under it. Flips when half of the bar is uncovered, the same point at which the Jobs scrim is half faded in.
 * An unmeasured header counts as covering: it sits at the top until the first scroll.
 */
internal fun headerCoversStatusBar(
    headerPx: Int,
    statusPx: Int,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffset: Int
): Boolean {
    if (firstVisibleItemIndex > 0) return false
    return headerPx <= 0 || firstVisibleItemScrollOffset < headerPx - statusPx / 2
}

private tailrec fun Context.findWindow(): Window? = when (this) {
    is Activity -> window
    is ContextWrapper -> baseContext.findWindow()
    else -> null
}

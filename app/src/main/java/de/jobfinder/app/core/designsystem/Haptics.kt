package de.jobfinder.app.core.designsystem

import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Single haptic vocabulary: one tick per user action, played through LocalHapticFeedback so the
 * system "touch feedback" setting decides whether anything vibrates.
 */
object JobFinderHaptics {
    /** Switching to another bottom-navigation tab. */
    val TabSelect = HapticFeedbackType.SegmentTick

    /** A successfully started primary action. */
    val Confirm = HapticFeedbackType.Confirm

    /** Chips and bookmarks: [on] is the state the control switches to. */
    fun toggle(on: Boolean) = if (on) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
}

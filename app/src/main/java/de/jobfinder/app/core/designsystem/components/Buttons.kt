package de.jobfinder.app.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderHaptics
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.collectPressScale
import de.jobfinder.app.core.designsystem.JobFinderSpacing

/**
 * Standard primary pill button (56dp height, full pill shape).
 */
@Composable
fun PrimaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    trailingIcon: Int? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = interactionSource.collectPressScale(pressed = JobFinderMotion.PressButton)
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .height(56.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clip(JobFinderShapes.Pill)
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(JobFinderHaptics.Confirm)
                    onClick()
                }
            ),
        shape = JobFinderShapes.Pill,
        color = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f),
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = JobFinderSpacing.lg)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor,
                    textAlign = TextAlign.Center
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(JobFinderSpacing.xs))
                    Icon(
                        painter = painterResource(id = trailingIcon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                }
            }
        }
    }
}

/**
 * Secondary tonal pill button (48-56dp height).
 */
@Composable
fun SecondaryTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = interactionSource.collectPressScale(pressed = JobFinderMotion.PressButton)

    Surface(
        modifier = modifier
            .height(48.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clip(JobFinderShapes.Pill)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        shape = JobFinderShapes.Pill,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Box(
            modifier = Modifier.padding(horizontal = JobFinderSpacing.md),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = contentColor
            )
        }
    }
}

/**
 * Bookmark toggle button enforcing 48x48dp minimum touch target.
 * Outline when not favorite, filled when favorite.
 */
@Composable
fun JobBookmarkButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    tint: Color = if (isFavorite) JobFinderColors.PrimaryLight else JobFinderColors.OnSurfaceVariantLight
) {
    val description = if (isFavorite) "Job gemerkt" else "Job merken"
    val iconRes = if (isFavorite) R.drawable.jf_ic_bookmark_filled else R.drawable.jf_ic_bookmark
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale = interactionSource.collectPressScale(pressed = JobFinderMotion.PressChip)
    // Pops only when a job is newly saved, not when an already saved card first appears.
    val popScale = remember { Animatable(1f) }
    var wasFavorite by remember { mutableStateOf(isFavorite) }
    LaunchedEffect(isFavorite) {
        if (isFavorite && !wasFavorite) {
            popScale.animateTo(JobFinderMotion.BookmarkPop, JobFinderMotion.effects(100))
            popScale.animateTo(1f, JobFinderMotion.spatial())
        }
        wasFavorite = isFavorite
    }

    Box(
        modifier = modifier
            .size(JobFinderSpacing.MinTouchTarget)
            .graphicsLayer { scaleX = pressScale.value; scaleY = pressScale.value }
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(JobFinderHaptics.toggle(on = !isFavorite))
                    onToggle()
                }
            )
            .semantics {
                contentDescription = description
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(22.dp)
                .graphicsLayer { scaleX = popScale.value; scaleY = popScale.value },
            tint = tint
        )
    }
}

/**
 * Connected CTA Button used in Onboarding.
 * Symmetrical 56dp pill fusing a text button with a circular action cap containing an arrow glyph.
 */
@Composable
fun ConnectedCtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = interactionSource.collectPressScale(pressed = JobFinderMotion.PressButton)

    Surface(
        modifier = modifier
            .height(56.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clip(JobFinderShapes.Pill)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        shape = JobFinderShapes.Pill,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            modifier = Modifier.padding(start = JobFinderSpacing.lg, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(JobFinderSpacing.md))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.jf_ic_arrow_forward),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

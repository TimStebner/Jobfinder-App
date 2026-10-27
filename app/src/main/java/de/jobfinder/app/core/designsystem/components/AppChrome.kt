package de.jobfinder.app.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderElevation
import de.jobfinder.app.core.designsystem.JobFinderHaptics
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.designsystem.collectPressScale
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

enum class AppNavItem(val label: String, val iconRes: Int) {
    Home("Suche", R.drawable.jf_ic_search),
    Jobs("Jobs", R.drawable.jf_ic_briefcase),
    Favorites("Favoriten", R.drawable.jf_ic_bookmark)
}

/**
 * Top app bar with inset awareness, title, optional back button, and optional trailing actions.
 */
@Composable
fun JobFinderTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = JobFinderSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(JobFinderSpacing.MinTouchTarget)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_arrow_back),
                            contentDescription = "Zurück",
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(JobFinderSpacing.xs))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor,
                    maxLines = 1
                )
            }
            if (actions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
    }
}

/**
 * Floating bottom navigation pill bar with stadium capsule shape. One amber indicator glides between
 * the tabs (position and width spring); tabs keep a constant size so selecting never reflows the row.
 */
@Composable
fun FloatingBottomNavigation(
    items: List<AppNavItem>,
    currentDestination: AppNavItem,
    onNavigate: (AppNavItem) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState = rememberHazeState()
) {
    val navGlassStyle = HazeMaterials.thin(
        containerColor = Color.White
    ).then {
        blurRadius(24.dp)
        noiseFactor(0f)
    }

    val haptic = LocalHapticFeedback.current
    val pillShape = RoundedCornerShape(percent = 50)
    val activeTextColor = Color(0xFF0F172A)
    val inactiveTextColor = Color(0xFF64748B)

    // Tab bounds in the item row's coordinates; the indicator is placed in the same space.
    val tabBounds = remember { mutableStateMapOf<AppNavItem, Rect>() }
    val indicatorLeft = remember { Animatable(0f) }
    val indicatorWidth = remember { Animatable(0f) }
    val selectedBounds = tabBounds[currentDestination]
    LaunchedEffect(selectedBounds) {
        val bounds = selectedBounds ?: return@LaunchedEffect
        if (indicatorWidth.value == 0f) {
            // First measurement: place without animating so the pill never flies in from the origin.
            indicatorLeft.snapTo(bounds.left)
            indicatorWidth.snapTo(bounds.width)
        } else {
            launch { indicatorLeft.animateTo(bounds.left, JobFinderMotion.spatial()) }
            launch { indicatorWidth.animateTo(bounds.width, JobFinderMotion.spatial()) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = JobFinderSpacing.md)
            .padding(bottom = JobFinderSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 375.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = JobFinderElevation.FloatingBar,
                    shape = pillShape,
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .clip(pillShape)
                .hazeBlur(
                    input = HazeInput.Sources(hazeState),
                    style = navGlassStyle,
                    performanceMode = HazePerformanceMode.Quality,
                    expandLayerBounds = true
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.65f),
                    shape = pillShape
                ),
            shape = pillShape,
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)) {
                Box(
                    modifier = Modifier
                        // Animated values are read in the layout phase: no recomposition per frame.
                        .offset { IntOffset(indicatorLeft.value.roundToInt(), 0) }
                        // After the offset, so the semantics bounds include the animated position.
                        .testTag("nav_indicator")
                        .layout { measurable, _ ->
                            val width = indicatorWidth.value.roundToInt()
                            val height = selectedBounds?.height?.roundToInt() ?: 0
                            val placeable = measurable.measure(Constraints.fixed(width, height))
                            layout(width, height) { placeable.place(0, 0) }
                        }
                        .shadow(
                            elevation = 4.dp,
                            shape = pillShape,
                            spotColor = JobFinderColors.Amber.copy(alpha = 0.4f)
                        )
                        .background(color = JobFinderColors.Amber, shape = pillShape)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { item ->
                        val isSelected = item == currentDestination
                        val itemContentColor by animateColorAsState(
                            targetValue = if (isSelected) activeTextColor else inactiveTextColor,
                            animationSpec = JobFinderMotion.effects(JobFinderMotion.MediumMs),
                            label = "nav_item_content_color"
                        )
                        val interactionSource = remember { MutableInteractionSource() }
                        val pressScale = interactionSource.collectPressScale(pressed = JobFinderMotion.PressTab)

                        val iconRes = if (isSelected && item == AppNavItem.Favorites) {
                            R.drawable.jf_ic_bookmark_filled
                        } else {
                            item.iconRes
                        }

                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    val bounds = coordinates.boundsInParent()
                                    if (tabBounds[item] != bounds) tabBounds[item] = bounds
                                }
                                .graphicsLayer { scaleX = pressScale.value; scaleY = pressScale.value }
                                .defaultMinSize(minHeight = 48.dp)
                                .clip(pillShape)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current,
                                    role = Role.Tab,
                                    onClick = {
                                        if (!isSelected) haptic.performHapticFeedback(JobFinderHaptics.TabSelect)
                                        onNavigate(item)
                                    }
                                )
                                .semantics {
                                    selected = isSelected
                                    role = Role.Tab
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = itemContentColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    ),
                                    color = itemContentColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Sticky action bar pinned to the bottom of the screen (e.g. for Job Details).
 * Hosts a bookmark button and primary apply button with Level 2 elevation and insets.
 */
@Composable
fun StickyJobActionBar(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier,
    applyButtonText: String = "Zum Angebot",
    isApplying: Boolean = false,
    hasOfferUrl: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = JobFinderElevation.FloatingBar,
                spotColor = Color.Black.copy(alpha = 0.10f)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .fillMaxWidth()
                .height(84.dp)
                .padding(horizontal = JobFinderSpacing.lg, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(JobFinderSpacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F5F9))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                JobBookmarkButton(
                    isFavorite = isFavorite,
                    onToggle = onToggleFavorite,
                    tint = if (isFavorite) JobFinderColors.ActionBlue else Color(0xFF475569)
                )
            }

            PrimaryPillButton(
                text = applyButtonText,
                onClick = onApplyClick,
                enabled = hasOfferUrl,
                isLoading = isApplying,
                trailingIcon = R.drawable.jf_ic_external_link,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Branded blue header bar matching Stitch Jobs/Favorites designs.
 * Height 64dp, background #1D4ED8, menu icon, centered JobFinder brand with white dot, profile icon.
 */
@Composable
fun JobFinderBrandedHeader(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = JobFinderColors.HeaderBlue,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxWidth()
                .padding(horizontal = JobFinderSpacing.lg, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu Button (48dp touch target)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(role = Role.Button, onClick = onMenuClick)
                        .semantics { contentDescription = "Menü" },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_grid),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Centered JobFinder Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Text(
                        text = "JobFinder",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }

                // Profile Button (48dp touch target)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(role = Role.Button, onClick = onProfileClick)
                        .semantics { contentDescription = "Profil" },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_profile),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}



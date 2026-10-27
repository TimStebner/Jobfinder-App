package de.jobfinder.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderHaptics
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.designsystem.collectPressScale
import de.jobfinder.app.core.model.EmploymentType
import de.jobfinder.app.core.model.JobSource
import de.jobfinder.app.core.model.SearchFilters
import de.jobfinder.app.core.model.WorkMode
import kotlinx.coroutines.launch

/**
 * Pill-shaped filter chip with 48dp touch target and explicit selected/unselected styling.
 */
@Composable
fun JobFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    enabled: Boolean = true,
    singleChoice: Boolean = false
) {
    val backgroundColor = when {
        !enabled -> MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
        selected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainer
    }

    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = if (selected && enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    val interactionSource = remember { MutableInteractionSource() }
    val scale = interactionSource.collectPressScale(pressed = JobFinderMotion.PressChip)
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .height(JobFinderSpacing.MinTouchTarget)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Checkbox,
                onClick = {
                    // In a single-choice group the selected chip stays selected: nothing toggled, nothing to feel.
                    if (!(singleChoice && selected)) haptic.performHapticFeedback(JobFinderHaptics.toggle(on = !selected))
                    onClick()
                }
            )
            .semantics {
                this.selected = selected
                this.role = Role.Checkbox
            },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .height(38.dp)
                .clip(JobFinderShapes.Pill)
                .border(
                    width = if (selected) 1.5.dp else 0.dp,
                    color = borderColor,
                    shape = JobFinderShapes.Pill
                ),
            shape = JobFinderShapes.Pill,
            color = backgroundColor,
            contentColor = contentColor
        ) {
            Row(
                modifier = Modifier.padding(horizontal = JobFinderSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = contentColor
                    )
                    Spacer(modifier = Modifier.width(JobFinderSpacing.xs))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = contentColor
                )
            }
        }
    }
}

/**
 * Close action for a [ModalBottomSheet]: slides the sheet out, then calls [onDismiss]. Removing the sheet
 * straight away (Apply, close button) would cut off its exit animation; only drag and scrim do it right.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSheetCloser(sheetState: SheetState, onDismiss: () -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    return { scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() } }
}

/**
 * Modal bottom sheet with draft filter copy.
 * Commits on Apply ("Ergebnisse anzeigen"), discards on dismiss/scrim/close.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    initialFilters: SearchFilters,
    totalResultCount: Int?,
    onApply: (SearchFilters) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    // Hold an isolated draft copy inside the sheet
    var draftFilters by remember(initialFilters) { mutableStateOf(initialFilters) }
    val close = rememberSheetCloser(sheetState, onDismiss)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = JobFinderShapes.FilterSheet,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = JobFinderSpacing.lg)
                .padding(bottom = JobFinderSpacing.xl)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = JobFinderSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(
                    onClick = { draftFilters = SearchFilters() }
                ) {
                    Text(
                        text = "Zurücksetzen",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.lg)
            ) {
                // Section: Umkreis
                FilterSection(title = "Umkreis") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)
                    ) {
                        listOf(10, 25, 50, 100).forEach { km ->
                            JobFilterChip(
                                text = "$km km",
                                selected = draftFilters.radiusKm == km,
                                singleChoice = true,
                                onClick = {
                                    draftFilters = draftFilters.copy(radiusKm = km)
                                }
                            )
                        }
                    }
                }

                // Section: Anstellungsart
                FilterSection(title = "Anstellungsart") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)
                    ) {
                        listOf(
                            EmploymentType.FULL_TIME to "Vollzeit",
                            EmploymentType.PART_TIME to "Teilzeit",
                            EmploymentType.MINIJOB to "Minijob"
                        ).forEach { (type, label) ->
                            val isSelected = draftFilters.employmentTypes.contains(type)
                            JobFilterChip(
                                text = label,
                                selected = isSelected,
                                onClick = {
                                    val updated = if (isSelected) {
                                        draftFilters.employmentTypes - type
                                    } else {
                                        draftFilters.employmentTypes + type
                                    }
                                    draftFilters = draftFilters.copy(employmentTypes = updated)
                                }
                            )
                        }
                    }
                }

                // Section: Arbeitsmodell
                FilterSection(title = "Arbeitsmodell") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)
                    ) {
                        JobFilterChip(
                            text = "Nur Homeoffice / Remote",
                            selected = draftFilters.remoteOnly,
                            onClick = {
                                draftFilters = draftFilters.copy(remoteOnly = !draftFilters.remoteOnly)
                            }
                        )
                    }
                }

                // Section: Portale
                FilterSection(title = "Jobbörsen") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)
                    ) {
                        listOf(
                            JobSource.STEPSTONE to "StepStone",
                            JobSource.ARBEITSAGENTUR to "Arbeitsagentur",
                            JobSource.KLEINANZEIGEN to "Kleinanzeigen",
                            JobSource.XING to "Xing"
                        ).forEach { (source, label) ->
                            val isSelected = draftFilters.sources.contains(source)
                            JobFilterChip(
                                text = label,
                                selected = isSelected,
                                onClick = {
                                    val activeSources = draftFilters.sources.intersect(JobSource.activeSearchSources)
                                    val updated = if (isSelected) {
                                        if (activeSources.size > 1) draftFilters.sources - source else draftFilters.sources
                                    } else {
                                        draftFilters.sources + source
                                    }
                                    draftFilters = draftFilters.copy(sources = updated)
                                }
                            )
                        }
                        JobFilterChip(
                            text = "LinkedIn (Derzeit nicht verfügbar)",
                            selected = false,
                            enabled = false,
                            onClick = {}
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(JobFinderSpacing.lg))

            // Apply Button
            val applyText = if (totalResultCount != null) {
                "Ergebnisse anzeigen ($totalResultCount)"
            } else {
                "Ergebnisse anzeigen"
            }

            PrimaryPillButton(
                text = applyText,
                onClick = {
                    onApply(draftFilters)
                    close()
                }
            )
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

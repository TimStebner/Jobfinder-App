package de.jobfinder.app.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.core.model.StepStoneFitLevel

/**
 * Immersive Detail Hero with 42dp bottom corner radius, 3-stop vertical gradient,
 * centered 96dp company badge, centered title/company/salary, and pill badges.
 */
@Composable
fun JobDetailHero(
    job: JobUiModel,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = JobFinderShapes.DetailHero,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(JobFinderColors.DetailGradient)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = JobFinderSpacing.lg)
                .padding(top = 12.dp, bottom = 40.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Navigation Bar: Back button, Details title, Bookmark button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBackButton) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.20f))
                                .clickable(
                                    role = Role.Button,
                                    onClick = onBackClick
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_arrow_back),
                                contentDescription = "Zurück",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(44.dp))
                    }

                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = Color.White.copy(alpha = 0.95f)
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        JobBookmarkButton(
                            isFavorite = job.isFavorite,
                            onToggle = onToggleFavorite,
                            tint = if (job.isFavorite) JobFinderColors.Coral else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Centered 96dp Company Logo Badge with outer ring and gradient inner disc
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFEFF6FF), Color(0xFFE0E7FF))
                                )
                            )
                            .border(1.dp, Color(0xFFDBEAFE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = job.companyInitials ?: "JF",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp
                                ),
                                color = Color(0xFF174A9C)
                            )
                            val secondaryText = job.company?.let { c ->
                                val parts = c.trim().split("\\s+".toRegex())
                                if (parts.size > 1) parts[1].uppercase() else null
                            }
                            if (secondaryText != null) {
                                Text(
                                    text = secondaryText.take(8),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Job Details Overview in Hero: Title, Company, Salary
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 28.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if (job.company != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = job.company.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color(0xFFDBEAFE),
                        textAlign = TextAlign.Center
                    )
                }

                if (job.salaryFormatted != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = job.salaryFormatted,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                // Centered Filter / Pill Badges
                val categoryText = job.categoryLabel
                val workingTimeText = job.workingTimeBadgeText
                val employmentText = when (job.employmentBadgeText) {
                    "INTERNSHIP" -> "Praktikum"
                    "FULL TIME" -> "Vollzeit"
                    "PART TIME" -> "Teilzeit"
                    "MINIJOB" -> "Minijob"
                    "CONTRACT" -> "Vertrag"
                    "AUSBILDUNG" -> "Ausbildung"
                    else -> job.employmentBadgeText
                }

                val workModeText = when (job.workModeBadgeText) {
                    "VOR ORT" -> "Vor Ort"
                    "REMOTE" -> "Remote"
                    "HYBRID" -> "Hybrid"
                    else -> job.workModeBadgeText
                }

                val hasAnyBadge = categoryText != null || workingTimeText != null || employmentText != null || workModeText != null
                if (hasAnyBadge) {
                    Spacer(modifier = Modifier.height(20.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (categoryText != null) {
                            Surface(
                                shape = JobFinderShapes.Pill,
                                color = Color.White.copy(alpha = 0.20f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.30f))
                            ) {
                                Text(
                                    text = categoryText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                        if (workingTimeText != null) {
                            Surface(
                                shape = JobFinderShapes.Pill,
                                color = Color.Black.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = workingTimeText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                        if (employmentText != null) {
                            Surface(
                                shape = JobFinderShapes.Pill,
                                color = Color.Black.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = employmentText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                        if (workModeText != null) {
                            Surface(
                                shape = JobFinderShapes.Pill,
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Text(
                                    text = workModeText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF174A9C)
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                        if (job.stepStoneFit != null) {
                            val badgeBg = when (job.stepStoneFit) {
                                StepStoneFitLevel.PERFECT -> Color(0xFF10B981)
                                StepStoneFitLevel.GOOD -> Color(0xFF0EA5E9)
                                StepStoneFitLevel.POOR -> Color(0xFFF59E0B)
                            }
                            Surface(
                                shape = JobFinderShapes.Pill,
                                color = badgeBg.copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = "StepStone: ${job.stepStoneFit.badgeText}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
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
 * 2-column Quick Facts Grid (Standort & Quelle) matching Stitch mock geometry.
 */
@Composable
fun JobFactGrid(
    job: JobUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Standort Box
        val locationValue = job.locationFormatted ?: "Deutschland"
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDBEAFE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.jf_ic_pin),
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "Standort",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    )
                    Text(
                        text = locationValue,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1E293B)
                        ),
                        maxLines = 1
                    )
                }
            }
        }

        // Quelle Box
        // One source per line: merged postings list several portals and must not be clipped
        val sourceValue = if (job.sources.isNotEmpty()) {
            job.sources.distinct().joinToString("\n") { it.displayName }
        } else {
            "JobFinder"
        }
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFD1FAE5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.jf_ic_briefcase),
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "Quelle",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    )
                    Text(
                        text = sourceValue,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1E293B)
                        )
                    )
                }
            }
        }
    }
}

/**
 * Editorial section for overview, qualifications, and benefits.
 */
@Composable
fun JobDetailSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.2).sp
            ),
            color = Color(0xFF0F172A)
        )
        content()
    }
}

/**
 * Bullet point list with 8dp dark circles matching Stitch Qualifications section.
 */
@Composable
fun BulletPointList(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF475569)
                    )
                )
            }
        }
    }
}

/** Full skill list as wrapping chips (the cards only show the first three). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillChips(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { skill ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text(
                    text = skill,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color(0xFF334155)
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 2-column Benefits Grid with checkmarks matching Stitch "Was wir bieten" section.
 */
@Composable
fun BenefitsGrid(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    val chunked = items.chunked(2)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { benefit ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_check),
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = benefit,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155)
                                ),
                                maxLines = 2
                            )
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

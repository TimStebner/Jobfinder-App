package de.jobfinder.app.core.designsystem.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderElevation
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.designsystem.collectPressScale
import de.jobfinder.app.core.model.JobSource
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.core.model.StepStoneFitLevel

/**
 * Circular employer logo disc with initial fallback.
 */
@Composable
fun CompanyLogoDisc(
    initials: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    containerColor: Color = Color.White,
    textColor: Color = JobFinderColors.HeaderBlue
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials ?: "JF",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.38f).sp
            ),
            color = textColor
        )
    }
}

/**
 * Standard Job Card matching reference mockup and Stitch front-end specification.
 * Features 24dp rounded corners, soft resting shadow, squircle employer logo,
 * meta badges, nested lavender container with location/salary icons and white skill pills,
 * and full-width "Details ansehen" CTA button.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StandardJobCard(
    job: JobUiModel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale = interactionSource.collectPressScale(pressed = JobFinderMotion.PressCard)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = pressScale.value; scaleY = pressScale.value }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x2E0F172A),
                ambientColor = Color(0x140F172A)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JobFinderSpacing.md)
        ) {
            // Header Row: Logo, Title/Company + Date, Bookmark Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Squircle Employer Logo
                val isGoogle = job.company?.contains("Google", ignoreCase = true) == true
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE2E8F0).copy(alpha = 0.8f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGoogle) {
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(28.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Text(
                            text = job.companyInitials ?: "JF",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color(0xFF1D4ED8)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 22.sp
                        ),
                        color = Color(0xFF0F172A),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val dateText = formatPostedDate(job.postedDateText)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!job.company.isNullOrBlank()) {
                            Text(
                                text = job.company,
                                modifier = Modifier.weight(1f, fill = false),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF475569),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "  •  ",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = Color(0xFF64748B),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                JobBookmarkButton(
                    isFavorite = job.isFavorite,
                    onToggle = onToggleFavorite,
                    containerColor = Color(0xFFEFF6FF),
                    tint = if (job.isFavorite) Color(0xFF1D4ED8) else Color(0xFF64748B)
                )
            }

            // Badges Row (Category, Working Time, Employment, Work Mode, Source)
            val hasBadges = job.categoryLabel != null ||
                job.workingTimeBadgeText != null ||
                job.employmentBadgeText != null ||
                job.workModeBadgeText != null ||
                job.sources.isNotEmpty()
            if (hasBadges) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (job.categoryLabel != null) {
                        JobMetaBadge(
                            text = job.categoryLabel.uppercase(),
                            backgroundColor = Color(0xFFEFF6FF),
                            textColor = Color(0xFF1D4ED8)
                        )
                    }
                    if (job.workingTimeBadgeText != null) {
                        JobMetaBadge(
                            text = job.workingTimeBadgeText.uppercase(),
                            backgroundColor = Color(0xFFFEF3C7),
                            textColor = Color(0xFF92400E)
                        )
                    }
                    if (job.employmentBadgeText != null) {
                        JobMetaBadge(
                            text = job.employmentBadgeText.uppercase(),
                            backgroundColor = Color(0xFFFEF3C7),
                            textColor = Color(0xFF92400E)
                        )
                    }
                    if (job.workModeBadgeText != null) {
                        JobMetaBadge(
                            text = job.workModeBadgeText.uppercase(),
                            backgroundColor = Color(0xFFFEF3C7),
                            textColor = Color(0xFF92400E)
                        )
                    }
                    job.sources.distinct().forEach { src ->
                        JobMetaBadge(
                            text = src.name.uppercase(),
                            backgroundColor = Color(0xFFF1F5F9),
                            textColor = Color(0xFF475569)
                        )
                    }
                    if (job.stepStoneFit != null) {
                        val (fitBg, fitText) = when (job.stepStoneFit) {
                            StepStoneFitLevel.PERFECT -> Color(0xFFD1FAE5) to Color(0xFF065F46)
                            StepStoneFitLevel.GOOD -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
                            StepStoneFitLevel.POOR -> Color(0xFFFEF3C7) to Color(0xFF92400E)
                        }
                        JobMetaBadge(
                            text = "STEPSTONE: ${job.stepStoneFit.badgeText.uppercase()}",
                            backgroundColor = fitBg,
                            textColor = fitText
                        )
                    }
                }
            }

            // Nested Lavender Metadata Panel
            val hasLocation = !job.locationFormatted.isNullOrBlank()
            val hasSalary = !job.salaryFormatted.isNullOrBlank()
            val hasSkills = job.visibleSkills.isNotEmpty() || job.skillsOverflowCount > 0

            if (hasLocation || hasSalary || hasSkills) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF4F6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E7FF).copy(alpha = 0.8f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (hasLocation) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.jf_ic_pin),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF1D4ED8)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = job.locationFormatted.orEmpty(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    ),
                                    color = Color(0xFF334155),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (hasSalary) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.jf_ic_salary),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF1D4ED8)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = job.salaryFormatted.orEmpty(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = Color(0xFF1E293B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (hasSkills) {
                            FlowRow(
                                modifier = Modifier.padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                job.visibleSkills.forEach { skill ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                    ) {
                                        Text(
                                            text = skill,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 12.sp
                                            ),
                                            color = Color(0xFF334155),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                if (job.skillsOverflowCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.7f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "+${job.skillsOverflowCount}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            ),
                                            color = Color(0xFF64748B),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action CTA Button: "Details ansehen →"
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable(
                        role = Role.Button,
                        onClick = onClick
                    ),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1D4ED8)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Details ansehen",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.jf_ic_arrow_forward),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun JobMetaBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Featured Job Card with 32dp radius and full-bleed signature gradient background.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeaturedJobCard(
    job: JobUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String = "Noch 2 Tage",
    gradient: Brush = Brush.linearGradient(
        listOf(
            Color(0xFF174A9C),
            Color(0xFF2F6BD6)
        )
    )
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = JobFinderElevation.Card,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(32.dp))
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(JobFinderSpacing.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top row: Logo and Remaining Time Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompanyLogoDisc(
                        initials = job.companyInitials,
                        size = 48.dp,
                        containerColor = Color.White,
                        textColor = JobFinderColors.HeaderBlue
                    )

                    Surface(
                        shape = JobFinderShapes.Pill,
                        color = Color.White.copy(alpha = 0.20f)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(JobFinderSpacing.md))

                // Title
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Company & Salary
                Text(
                    text = listOfNotNull(job.company, job.salaryFormatted).joinToString(" • "),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Bottom Tag Row
                Spacer(modifier = Modifier.height(JobFinderSpacing.md))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.xs)
                ) {
                    if (job.employmentBadgeText != null) {
                        SemiTransparentTag(text = job.employmentBadgeText)
                    }
                    if (job.workModeBadgeText != null) {
                        SemiTransparentTag(text = job.workModeBadgeText)
                    }
                    if (!job.locationFormatted.isNullOrBlank()) {
                        SemiTransparentTag(text = job.locationFormatted)
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillPill(
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    isBold: Boolean = false
) {
    Surface(
        shape = JobFinderShapes.Pill,
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun SemiTransparentTag(
    text: String
) {
    Surface(
        shape = JobFinderShapes.Pill,
        color = Color.White.copy(alpha = 0.20f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

private fun formatPostedDate(raw: String?): String {
    if (raw.isNullOrBlank()) return "Vor 3 Tagen"
    val clean = raw.trim()
    try {
        val datePart = if (clean.length >= 10 && clean[4] == '-' && clean[7] == '-') clean.substring(0, 10) else null
        if (datePart != null) {
            val parsed = java.time.LocalDate.parse(datePart)
            val today = java.time.LocalDate.now()
            val days = java.time.temporal.ChronoUnit.DAYS.between(parsed, today)
            return when {
                days <= 0L -> "Heute"
                days == 1L -> "Gestern"
                days in 2L..30L -> "Vor $days Tagen"
                days > 30L -> "Vor ${days / 30} Monaten"
                else -> "Heute"
            }
        }
    } catch (_: Exception) {
        // Fallback to raw string
    }
    return clean
}

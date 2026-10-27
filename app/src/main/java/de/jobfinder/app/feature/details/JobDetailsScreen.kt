package de.jobfinder.app.feature.details

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.jobfinder.app.core.designsystem.headerCoversStatusBar
import de.jobfinder.app.core.designsystem.StatusBarIcons
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.designsystem.components.BenefitsGrid
import de.jobfinder.app.core.designsystem.components.BulletPointList
import de.jobfinder.app.core.designsystem.components.JobDetailHero
import de.jobfinder.app.core.designsystem.components.JobDetailSection
import de.jobfinder.app.core.designsystem.components.JobFactGrid
import de.jobfinder.app.core.designsystem.components.SkeletonBlock
import de.jobfinder.app.core.designsystem.components.SkillChips
import de.jobfinder.app.core.designsystem.components.StickyJobActionBar
import de.jobfinder.app.core.model.JobUiModel

/**
 * Pixel-faithful Job Details Screen implementing the 390 x 884 Stitch specification.
 * Features immersive 42dp hero, quick-facts grid, optional editorial sections,
 * sticky 84dp action bar, and adaptive width capping (720dp) for expanded layouts.
 */
@Composable
fun JobDetailsScreen(
    jobId: String,
    viewModel: JobDetailsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onOpenUrl: ((String) -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val actualOpenUrl: (String) -> Unit = onOpenUrl ?: { url ->
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    LaunchedEffect(jobId) {
        viewModel.loadJob(jobId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading && state.job == null) {
            JobDetailsSkeleton()
        } else if (state.job != null) {
            val job = state.job!!
            JobDetailsContent(
                job = job,
                isFavorite = state.isFavorite,
                onBackClick = onBackClick,
                onToggleFavorite = { viewModel.toggleFavorite() },
                onApplyClick = {
                    val effectiveUrl = job.effectiveApplicationUrl
                    if (!effectiveUrl.isNullOrBlank()) {
                        actualOpenUrl(effectiveUrl)
                    }
                },
                showBackButton = showBackButton
            )
        } else if (state.error != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.jf_ic_close),
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.error ?: "Job nicht gefunden",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Stateless Job Details content reusable across Screen, Multi-Pane, and Previews.
 */
@Composable
fun JobDetailsContent(
    job: JobUiModel,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true
) {
    val effectiveUrl = job.effectiveApplicationUrl
    val hasOfferUrl = !effectiveUrl.isNullOrBlank()

    // White status bar icons while the blue hero covers the bar, dark once the content scrolls under it
    val listState = rememberLazyListState()
    var heroPx by remember { mutableIntStateOf(0) }
    val statusTopPx = WindowInsets.statusBars.getTop(LocalDensity.current)
    val heroCoversStatusBar by remember {
        derivedStateOf {
            headerCoversStatusBar(heroPx, statusTopPx, listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
    }
    StatusBarIcons(whiteIcons = heroCoversStatusBar)

    Column(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 720.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                JobDetailHero(
                    job = job,
                    onBackClick = onBackClick,
                    onToggleFavorite = onToggleFavorite,
                    modifier = Modifier.onSizeChanged { heroPx = it.height },
                    showBackButton = showBackButton
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = JobFinderSpacing.lg)
                        .padding(top = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    job.detailNotice?.let { notice ->
                        JobDetailSection(title = "Hinweis zur Beschreibung") {
                            Text(text = notice, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    // Quick Facts Grid
                    JobFactGrid(job = job)

                    // Job Overview Section
                    if (!job.description.isNullOrBlank()) {
                        JobDetailSection(title = "Stellenbeschreibung") {
                            Text(
                                text = job.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = Color(0xFF475569)
                                )
                            )
                        }
                    }

                    // Responsibilities Section
                    if (job.responsibilities.isNotEmpty()) {
                        JobDetailSection(title = "Ihre Aufgaben") {
                            BulletPointList(items = job.responsibilities)
                        }
                    }

                    // Qualifications Section
                    if (job.qualifications.isNotEmpty()) {
                        JobDetailSection(title = "Anforderungen") {
                            BulletPointList(items = job.qualifications)
                        }
                    }

                    if (job.skills.isNotEmpty()) {
                        JobDetailSection(title = "Skills") {
                            SkillChips(items = job.skills)
                        }
                    }

                    // Benefits Section
                    if (job.benefits.isNotEmpty()) {
                        JobDetailSection(title = "Was wir bieten") {
                            BenefitsGrid(items = job.benefits)
                        }
                    }

                    // Contact Section
                    val hasContact = !job.contactName.isNullOrBlank() || !job.contactEmail.isNullOrBlank() || !job.contactPhone.isNullOrBlank()
                    if (hasContact) {
                        JobDetailSection(title = "Kontakt & Ansprechpartner") {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                job.contactName?.let { name ->
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF0F172A)
                                        )
                                    )
                                }
                                job.contactEmail?.let { email ->
                                    Text(
                                        text = email,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = JobFinderColors.ActionBlue
                                        )
                                    )
                                }
                                job.contactPhone?.let { phone ->
                                    Text(
                                        text = phone,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF475569)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Sticky Action Bar
        StickyJobActionBar(
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite,
            onApplyClick = onApplyClick,
            hasOfferUrl = hasOfferUrl,
            applyButtonText = "Zum Angebot"
        )
    }
}

/** Placeholder while a job nobody cached yet is loading: hero block, title, company and body lines. */
@Composable
private fun JobDetailsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("details_skeleton")
            .semantics(mergeDescendants = true) { contentDescription = "Wird geladen" }
    ) {
        SkeletonBlock(Modifier.fillMaxWidth().height(180.dp))
        Column(
            modifier = Modifier.padding(JobFinderSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.md)
        ) {
            SkeletonBlock(Modifier.fillMaxWidth(0.7f).height(28.dp))
            SkeletonBlock(Modifier.fillMaxWidth(0.45f).height(18.dp))
            repeat(3) { SkeletonBlock(Modifier.fillMaxWidth().height(14.dp)) }
        }
    }
}

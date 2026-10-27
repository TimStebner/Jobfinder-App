package de.jobfinder.app.feature.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.jobfinder.app.core.designsystem.StatusBarIcons
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.jobfinder.app.R
import androidx.compose.ui.platform.testTag
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.designsystem.components.JobFinderBrandedHeader
import de.jobfinder.app.core.designsystem.components.PrimaryPillButton
import de.jobfinder.app.core.designsystem.components.SkeletonBlock
import de.jobfinder.app.core.designsystem.components.StandardJobCard
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.navigation.jobSharedBounds

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToJobDetails: (jobId: String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToSearch: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesScreenContent(
        favoriteJobs = state.favoriteJobs,
        isLoading = state.isLoading,
        onJobClick = onNavigateToJobDetails,
        onToggleFavorite = { viewModel.toggleFavorite(it) },
        onExploreJobs = onNavigateToSearch,
        modifier = modifier
    )
}

@Composable
fun FavoritesScreenContent(
    favoriteJobs: List<JobUiModel>,
    isLoading: Boolean,
    onJobClick: (jobId: String) -> Unit,
    onToggleFavorite: (jobId: String) -> Unit,
    onExploreJobs: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The blue header is fixed, so the status bar always sits on blue
    StatusBarIcons(whiteIcons = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FD))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Blue App Header Bar: Menu, JobFinder Brand, Profile
            JobFinderBrandedHeader()

            // Favorites Header Section: Title, Count Badge, Description, Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = JobFinderSpacing.lg)
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                // Title and Count Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Favoriten",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            fontSize = 28.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )

                    Surface(
                        shape = JobFinderShapes.Pill,
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E7FF))
                    ) {
                        Text(
                            text = "${favoriteJobs.size} Jobs",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8),
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Deine gemerkten Stellenangebote auf einen Blick",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                )

                // Filter / Sort Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Surface(
                            shape = JobFinderShapes.Pill,
                            color = Color(0xFF1D4ED8),
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = "Alle (${favoriteJobs.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    item {
                        Surface(
                            shape = JobFinderShapes.Pill,
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = "Neueste zuerst",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155),
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    item {
                        Surface(
                            shape = JobFinderShapes.Pill,
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = "Full-Time",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155),
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    item {
                        Surface(
                            shape = JobFinderShapes.Pill,
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = "Remote",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155),
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Body: Loading, Empty, or Favorites List
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = JobFinderSpacing.lg, vertical = JobFinderSpacing.md)
                        .testTag("favorites_skeleton")
                        .semantics(mergeDescendants = true) { contentDescription = "Wird geladen" },
                    verticalArrangement = Arrangement.spacedBy(JobFinderSpacing.md)
                ) {
                    repeat(3) { SkeletonBlock(Modifier.fillMaxWidth().height(96.dp)) }
                }
            } else if (favoriteJobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_bookmark),
                                contentDescription = null,
                                tint = Color(0xFF1D4ED8),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Keine Favoriten gespeichert",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontSize = 18.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tippe bei interessanten Stellenangeboten auf das Lesezeichen-Symbol, um sie hier wiederzufinden.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF64748B),
                                fontSize = 14.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryPillButton(
                            text = "Jetzt Jobs entdecken",
                            onClick = onExploreJobs,
                            modifier = Modifier.fillMaxWidth(0.75f)
                        )
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val columns = when {
                        maxWidth >= 1000.dp -> 3
                        maxWidth >= 600.dp -> 2
                        else -> 1
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = JobFinderSpacing.lg,
                            vertical = 12.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(favoriteJobs, key = { it.id }) { job ->
                            StandardJobCard(
                                job = job,
                                onClick = { onJobClick(job.id) },
                                onToggleFavorite = { onToggleFavorite(job.id) },
                                modifier = Modifier.animateItem().jobSharedBounds(job.id)
                            )
                        }

                        // Spacer for bottom bar padding
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Spacer(modifier = Modifier.height(110.dp))
                        }
                    }
                }
            }
        }
    }
}

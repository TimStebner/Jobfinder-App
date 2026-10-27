package de.jobfinder.app.feature.jobs

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.lazy.LazyRow
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.model.JobSource
import de.jobfinder.app.core.model.SourceSearchState
import de.jobfinder.app.core.model.SourceSearchStatus
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.jobfinder.app.core.designsystem.headerCoversStatusBar
import de.jobfinder.app.core.designsystem.StatusBarIcons
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.sign

import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.designsystem.components.JobFinderBrandedHeader
import de.jobfinder.app.core.designsystem.components.StandardJobCard
import de.jobfinder.app.core.model.SearchError
import de.jobfinder.app.core.model.SearchFilters
import de.jobfinder.app.navigation.jobSharedBounds

@Composable
fun JobsScreen(
    viewModel: SearchViewModel,
    onNavigateToJobDetails: (jobId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    JobsScreenContent(
        state = state,
        onJobClick = onNavigateToJobDetails,
        onToggleFavorite = { viewModel.toggleFavorite(it) },
        onApplyClientFilters = { viewModel.applyClientFilters(it) },
        onSelectSortOrder = { viewModel.setSortOrder(it) },
        onPreviousPage = { viewModel.previousPage() },
        onNextPage = { viewModel.nextPage() },
        onRetry = { viewModel.retry() },
        onRetryLoadResults = { viewModel.retryLoadResults() },
        modifier = modifier
    )
}

@Composable
fun JobsScreenContent(
    state: SearchUiState,
    onJobClick: (jobId: String) -> Unit,
    onToggleFavorite: (jobId: String) -> Unit,
    onApplyFilters: (SearchFilters) -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier
) = JobsScreenContent(
    state = state,
    onJobClick = onJobClick,
    onToggleFavorite = onToggleFavorite,
    onNextPage = onLoadNextPage,
    modifier = modifier
)

@Composable
fun JobsScreenContent(
    state: SearchUiState,
    onJobClick: (jobId: String) -> Unit,
    onToggleFavorite: (jobId: String) -> Unit,
    onApplyFilters: (SearchFilters, String) -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier
) = JobsScreenContent(
    state = state,
    onJobClick = onJobClick,
    onToggleFavorite = onToggleFavorite,
    onNextPage = onLoadNextPage,
    modifier = modifier
)

private enum class JobsBody { Loading, Error, Empty, Results }

private fun jobsBody(state: SearchUiState): JobsBody = when {
    state.isSearching && state.jobs.isEmpty() -> JobsBody.Loading
    !state.isSearching && state.jobs.isEmpty() && state.error != null -> JobsBody.Error
    !state.isSearching && state.jobs.isEmpty() -> JobsBody.Empty
    else -> JobsBody.Results
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreenContent(
    state: SearchUiState,
    onJobClick: (jobId: String) -> Unit,
    onToggleFavorite: (jobId: String) -> Unit,
    modifier: Modifier = Modifier,
    onApplyClientFilters: (ClientJobFilters) -> Unit = {},
    onSelectSortOrder: (JobSortOrder) -> Unit = {},
    onPreviousPage: () -> Unit = {},
    onNextPage: () -> Unit = {},
    onRetry: () -> Unit = {},
    onRetryLoadResults: () -> Unit = {}
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    LaunchedEffect(state.currentPage) {
        gridState.scrollToItem(0)
    }

    LaunchedEffect(state.sortOrder, state.clientFilters) {
        gridState.scrollToItem(0)
    }

    val density = LocalDensity.current
    val statusTopPx = WindowInsets.statusBars.getTop(density)
    val pinnedTopPx = statusTopPx + with(density) { 8.dp.roundToPx() }
    // Blue header + search info height: the sort chips rest below it until they pin under the status bar
    var anchorPx by remember { mutableIntStateOf(0) }
    // Blue header alone: list content reaches the status bar once the header has scrolled off it
    var headerPx by remember { mutableIntStateOf(0) }
    // White status bar icons while the blue header covers the bar (always when it is fixed above an empty list)
    val headerOnStatusBar by remember {
        derivedStateOf {
            headerCoversStatusBar(headerPx, statusTopPx, gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset)
        }
    }
    StatusBarIcons(whiteIcons = state.jobs.isEmpty() || headerOnStatusBar)

    // Page swap: the new page's cards slide in from the side the user is heading to
    var shownPage by remember { mutableIntStateOf(state.currentPage) }
    var pageDir by remember { mutableIntStateOf(0) }
    val pageAnim = remember { Animatable(1f) }
    LaunchedEffect(state.currentPage) {
        // Only a real change animates: re-entering the screen (tabs, back from details) keeps the cards still
        if (state.currentPage != shownPage) {
            pageDir = (state.currentPage - shownPage).sign
            pageAnim.snapTo(0f)
            shownPage = state.currentPage
            pageAnim.animateTo(1f, tween(JobFinderMotion.MediumMs, easing = JobFinderMotion.EmphasizedDecelerate))
        }
    }

    // Settles a finger-released scroll that stopped mid-transition to its nearer end; flings only, so
    // programmatic scrolls such as scrollToItem(0) never trigger it
    val settle = remember(gridState) {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val offset = gridState.firstVisibleItemScrollOffset
                settleTargetPx(anchorPx, gridState.firstVisibleItemIndex, offset)?.let { target ->
                    gridState.animateScrollBy((target - offset).toFloat(), tween(350, easing = FastOutSlowInEasing))
                }
                return Velocity.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(JobsPageBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            if (state.jobs.isEmpty()) {
                JobFinderBrandedHeader()
                JobsSearchInfo(state = state)
                JobsSortChipRow(
                    state = state,
                    onSelectSortOrder = onSelectSortOrder,
                    onFilterClick = { showFilterSheet = true }
                )

                // Status Banners when results are empty (loading / error states)
                JobsStatusBanners(
                    state = state,
                    onRetry = onRetry,
                    onRetryLoadResults = onRetryLoadResults
                )
            }

            // Results List
            Crossfade(
                targetState = jobsBody(state),
                animationSpec = JobFinderMotion.effects(JobFinderMotion.MediumMs),
                label = "jobs_body"
            ) { body ->
                when (body) {
                    JobsBody.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(color = Color(0xFF1D4ED8))
                                Text(
                                    text = "Durchsuche Jobportale...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                    JobsBody.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            ) {
                                val title = when (state.error) {
                                    is SearchError.Timeout -> "Suche läuft auf dem Server möglicherweise weiter"
                                    is SearchError.NetworkUnavailable -> "Keine Internetverbindung"
                                    is SearchError.RateLimited -> "Zu viele Anfragen"
                                    is SearchError.Unauthorized -> "Nicht autorisiert"
                                    else -> "Fehler bei der Suche"
                                }
                                val description = when (state.error) {
                                    is SearchError.Timeout -> "Die Portale benötigen länger als erwartet. Du kannst den Status der Suche erneut prüfen."
                                    is SearchError.NetworkUnavailable -> "Bitte überprüfe deine Internetverbindung und versuche es erneut."
                                    is SearchError.RateLimited -> "Bitte warte einen Moment, bevor du eine neue Suche startest."
                                    is SearchError.Backend -> state.error.safeMessage
                                    else -> "Die Suchanfrage konnte nicht abgeschlossen werden."
                                }
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onRetry,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                                    shape = JobFinderShapes.Pill
                                ) {
                                    Text(if (state.error is SearchError.Timeout) "Status erneut prüfen" else "Wiederholen")
                                }
                            }
                        }
                    }
                    JobsBody.Empty -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.jf_ic_search),
                                        contentDescription = null,
                                        tint = Color(0xFF1D4ED8),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Keine Jobs gefunden",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Versuche deine Suchbegriffe oder Filter anzupassen.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    JobsBody.Results -> {
                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val columns = when {
                                maxWidth >= 1000.dp -> 3
                                maxWidth >= 600.dp -> 2
                                else -> 1
                            }

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(columns),
                                state = gridState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("jobs_grid")
                                    .nestedScroll(settle),
                                contentPadding = PaddingValues(
                                    start = JobFinderSpacing.lg,
                                    end = JobFinderSpacing.lg,
                                    bottom = 12.dp
                                ),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Blue header, search info and chip placeholder scroll with the list as one item (no gaps)
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Column {
                                        Column(modifier = Modifier.onSizeChanged { anchorPx = it.height }) {
                                            JobFinderBrandedHeader(
                                                modifier = Modifier
                                                    .layout { measurable, constraints ->
                                                        // Full bleed: the grid pads its items by lg on both sides, so widen the header by that padding
                                                        val bleed = JobFinderSpacing.lg.roundToPx() * 2
                                                        val width = constraints.maxWidth + bleed
                                                        val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                                                        layout(constraints.maxWidth, placeable.height) { placeable.place(-bleed / 2, 0) }
                                                    }
                                                    .testTag("jobs_header")
                                                    .onSizeChanged { headerPx = it.height }
                                            )
                                            JobsSearchInfo(
                                                state = state,
                                                modifier = Modifier.graphicsLayer {
                                                    alpha = infoAlpha(anchorPx, gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset)
                                                }
                                            )
                                        }
                                        // The sort chips are drawn by the overlay; this keeps the first card below them
                                        Spacer(modifier = Modifier.height(JobsSortChipRowHeight))
                                    }
                                }

                                // Scrollable Status Banners (scroll out of view smoothly)
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    JobsStatusBanners(
                                        state = state,
                                        onRetry = onRetry,
                                        onRetryLoadResults = onRetryLoadResults
                                    )
                                }

                                // Unsupported Remote Filter Warning Banner
                                if (state.error is SearchError.UnsupportedRemoteFilter) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color(0xFFFEF3C7),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Hinweis: Einige Filter werden von der aktuellen API-Version noch nicht unterstützt und wurden serverseitig übersprungen.",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF92400E),
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                modifier = Modifier.padding(14.dp)
                                            )
                                        }
                                    }
                                }

                                items(state.jobs, key = { it.id }) { job ->
                                    StandardJobCard(
                                        job = job,
                                        onClick = { onJobClick(job.id) },
                                        onToggleFavorite = { onToggleFavorite(job.id) },
                                        modifier = Modifier
                                            .graphicsLayer {
                                                // Hidden until the effect above has restarted the animation, so swapped cards never flash
                                                val p = if (shownPage != state.currentPage) 0f else pageAnim.value
                                                alpha = p
                                                translationX = (1f - p) * pageDir * 48.dp.toPx()
                                            }
                                            .jobSharedBounds(job.id)
                                    )
                                }

                                // Bottom Pagination Controls (50 Jobs per page)
                                if (state.totalPages > 1 || state.hasNextPage) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = JobFinderShapes.Pill,
                                                color = if (state.currentPage > 1) Color.White else Color(0xFFF1F5F9),
                                                border = if (state.currentPage > 1) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)) else null,
                                                modifier = Modifier
                                                    .height(40.dp)
                                                    .clip(JobFinderShapes.Pill)
                                                    .clickable(enabled = state.currentPage > 1) { onPreviousPage() }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.jf_ic_arrow_back),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp),
                                                        tint = if (state.currentPage > 1) Color(0xFF1E293B) else Color(0xFF94A3B8)
                                                    )
                                                    Text(
                                                        text = "Vorherige",
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = if (state.currentPage > 1) Color(0xFF1E293B) else Color(0xFF94A3B8)
                                                        )
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "Seite ${state.currentPage} von ${state.totalPages}",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF475569)
                                                )
                                            )

                                            val canGoNext = state.currentPage < state.totalPages || state.hasNextPage
                                            Surface(
                                                shape = JobFinderShapes.Pill,
                                                color = if (canGoNext) Color(0xFF1D4ED8) else Color(0xFFF1F5F9),
                                                modifier = Modifier
                                                    .height(40.dp)
                                                    .clip(JobFinderShapes.Pill)
                                                    .clickable(enabled = canGoNext) { onNextPage() }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = "Nächste",
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (canGoNext) Color.White else Color(0xFF94A3B8)
                                                        )
                                                    )
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.jf_ic_arrow_forward),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp),
                                                        tint = if (canGoNext) Color.White else Color(0xFF94A3B8)
                                                    )
                                                }
                                            }
                                        }
                                    }
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

        // Sort chips float over the list: they ride up with the header and pin under the status bar
        if (state.jobs.isNotEmpty() && anchorPx > 0) {
            // Cards scroll under the transparent status bar once the header has left it; keeps the clock readable
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .height(with(density) { pinnedTopPx.toDp() })
                    .graphicsLayer {
                        alpha = statusScrimAlpha(
                            headerPx, statusTopPx, gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset
                        )
                    }
                    .background(JobsPageBackground)
            )
            JobsSortChipRow(
                state = state,
                onSelectSortOrder = onSelectSortOrder,
                onFilterClick = { showFilterSheet = true },
                modifier = Modifier.offset {
                    IntOffset(
                        0,
                        chipOffsetY(anchorPx, gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset, pinnedTopPx)
                    )
                }
            )
        }

        // Filter Bottom Sheet
        if (showFilterSheet) {
            JobsFilterBottomSheet(
                initialFilters = state.clientFilters,
                // The sheet closes itself with its exit animation and then calls onDismiss.
                onApply = onApplyClientFilters,
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@Composable
private fun JobsStatusBanners(
    state: SearchUiState,
    onRetry: () -> Unit,
    onRetryLoadResults: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        state.queryNotice?.let { notice ->
            Text(
                text = notice,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF1D4ED8),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = JobFinderSpacing.lg, vertical = 6.dp)
            )
        }

        if (state.isSearching || state.isPolling) {
            SearchProgressCard(state = state)
        }

        val failedSources = state.sourceStates.filter {
            it.status == SourceSearchStatus.FAILED &&
                !it.source.name.equals("linkedin", ignoreCase = true)
        }
        val hasLimitedSource = state.sourceStates.any { it.source != JobSource.LINKEDIN && it.limitReached }
        if (!state.isSearching && !state.isPolling && (state.isPartial || hasLimitedSource)) {
            val hasTechError = failedSources.any {
                it.errorCode == "INTERNAL_ERROR" || it.errorCode == "BROWSER_UNAVAILABLE"
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEF3C7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = JobFinderSpacing.lg, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = buildPartialStatusText(state.sourceStates),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium
                        )
                    )
                    if (hasTechError) {
                        Button(
                            onClick = onRetry,
                            enabled = !state.isSearching,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD97706),
                                disabledContainerColor = Color(0xFFD97706).copy(alpha = 0.5f)
                            ),
                            shape = JobFinderShapes.Pill,
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                        ) {
                            Text(
                                text = "Suche erneut starten",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        if (state.resultLoadError != null && state.jobs.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEF2F2),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = JobFinderSpacing.lg, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weitere Ergebnisse konnten nicht geladen werden.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF991B1B),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = onRetryLoadResults,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = JobFinderShapes.Pill
                    ) {
                        Text("Nachladen", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchProgressCard(

    state: SearchUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFEFF6FF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = JobFinderSpacing.lg, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.totalSources > 0) {
                        "${state.completedSources} von ${state.totalSources} Portalen durchsucht"
                    } else {
                        "Suche läuft..."
                    },
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8)
                    )
                )
                Text(
                    text = if (state.resultsSeen > 0 && state.resultsSeen != state.effectiveJobs) {
                        "${state.effectiveJobs} gefunden (${state.resultsSeen} erfasste Einträge)"
                    } else {
                        "${state.effectiveJobs} gefunden"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
            }

            if (state.totalSources > 0) {
                val progressFraction = (state.completedSources.toFloat() / state.totalSources.toFloat()).coerceIn(0.05f, 1f)
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF2563EB),
                    trackColor = Color(0xFFDBEAFE)
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF2563EB),
                    trackColor = Color(0xFFDBEAFE)
                )
            }

            Text(
                text = "Live-Crawl aller Jobportale. Neue Suchen können bis zu 3 Minuten dauern.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            )
        }
    }
}

private fun JobSource.toDisplayLabel(): String = when (this) {
    JobSource.ARBEITSAGENTUR -> "Arbeitsagentur"
    JobSource.STEPSTONE -> "StepStone"
    JobSource.KLEINANZEIGEN -> "Kleinanzeigen"
    JobSource.XING -> "Xing"
    JobSource.LINKEDIN -> "LinkedIn"
}

private fun List<String>.joinToStringGerman(): String = when (size) {
    0 -> ""
    1 -> first()
    2 -> "${this[0]} und ${this[1]}"
    else -> "${dropLast(1).joinToString(", ")} und ${last()}"
}

internal fun buildPartialStatusText(sourceStates: List<SourceSearchState>): String {
    if (sourceStates.isEmpty()) {
        return "Hinweis: Es werden Teil-Ergebnisse angezeigt. Einige Quellen antworten verzögert oder waren nicht erreichbar."
    }

    val enabledStates = sourceStates.filter { it.source != JobSource.LINKEDIN }
    val available = enabledStates.filter { it.resultsSeen > 0 }
    val zeroHits = enabledStates.filter { it.status == SourceSearchStatus.COMPLETED && it.resultsSeen == 0 }
    val running = enabledStates.filter {
        (it.status == SourceSearchStatus.RUNNING || it.status == SourceSearchStatus.QUEUED) && it.resultsSeen == 0
    }
    val techFailed = enabledStates.filter {
        it.status == SourceSearchStatus.FAILED &&
            (it.errorCode == "INTERNAL_ERROR" || it.errorCode == "BROWSER_UNAVAILABLE")
    }
    val otherFailed = enabledStates.filter {
        it.status == SourceSearchStatus.FAILED &&
            it !in techFailed
    }
    val limited = enabledStates.filter {
        it.status == SourceSearchStatus.COMPLETED && it.limitReached
    }

    val parts = mutableListOf<String>()

    if (available.isNotEmpty()) {
        val names = available.map { it.source.toDisplayLabel() }.joinToStringGerman()
        parts.add("$names verfügbar.")
    }
    if (zeroHits.isNotEmpty()) {
        val names = zeroHits.map { it.source.toDisplayLabel() }.joinToStringGerman()
        parts.add("$names: Keine Treffer.")
    }
    if (running.isNotEmpty()) {
        val names = running.map { it.source.toDisplayLabel() }.joinToStringGerman()
        val verb = if (running.size > 1) "werden noch geladen." else "wird noch geladen."
        parts.add("$names $verb")
    }
    if (techFailed.isNotEmpty()) {
        val names = techFailed.map { it.source.toDisplayLabel() }.joinToStringGerman()
        val verb = if (techFailed.any { it.resultsSeen > 0 }) {
            "Suche wurde wegen einer technischen Störung nicht abgeschlossen; vorhandene Treffer bleiben verfügbar."
        } else if (techFailed.size > 1) "konnten wegen einer technischen Störung nicht geladen werden." else "konnte wegen einer technischen Störung nicht geladen werden."
        parts.add("$names $verb")
    }
    if (otherFailed.isNotEmpty()) {
        val names = otherFailed.map { it.source.toDisplayLabel() }.joinToStringGerman()
        val verb = if (otherFailed.any { it.resultsSeen > 0 }) {
            "Suche wurde nicht abgeschlossen; vorhandene Treffer bleiben verfügbar."
        } else if (otherFailed.size > 1) "konnten nicht erreicht werden." else "konnte nicht erreicht werden."
        parts.add("$names $verb")
    }
    if (limited.isNotEmpty()) {
        val names = limited.map { it.source.toDisplayLabel() }.joinToStringGerman()
        parts.add("$names: Ergebnisse durch Quellenlimit begrenzt.")
    }

    if (parts.isEmpty()) {
        return "Hinweis: Es werden Teil-Ergebnisse angezeigt. Einige Quellen antworten verzögert oder waren nicht erreichbar."
    }

    val prefix = if (limited.isNotEmpty() && techFailed.isEmpty() && otherFailed.isEmpty()) {
        "Begrenzte Ergebnisse: "
    } else "Teilergebnisse: "
    return prefix + parts.joinToString(" ")
}

/** Top of the sort chips in px: they ride under the search info, then pin at [pinnedTopPx] once [anchorPx] has scrolled away. */
internal fun chipOffsetY(
    anchorPx: Int,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffset: Int,
    pinnedTopPx: Int
): Int {
    if (firstVisibleItemIndex > 0) return pinnedTopPx
    return maxOf(pinnedTopPx, anchorPx - firstVisibleItemScrollOffset)
}

/** Search info fades out over the first [anchorPx] px of scrolling; it stays visible until measured. */
internal fun infoAlpha(anchorPx: Int, firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int): Float {
    if (anchorPx <= 0) return 1f
    if (firstVisibleItemIndex > 0) return 0f
    return (1f - firstVisibleItemScrollOffset.toFloat() / anchorPx).coerceIn(0f, 1f)
}

/**
 * Status bar scrim: off while the blue header still covers the status bar, then fades in over the bar's own
 * height as list content passes under it. Starting any earlier would wash out the header.
 */
internal fun statusScrimAlpha(
    headerPx: Int,
    statusPx: Int,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffset: Int
): Float {
    if (firstVisibleItemIndex > 0) return 1f
    if (headerPx <= 0 || statusPx <= 0) return 0f
    return ((firstVisibleItemScrollOffset - (headerPx - statusPx)).toFloat() / statusPx).coerceIn(0f, 1f)
}

/** Nearer end for a fling that stopped inside the header range; null when there is nothing to settle. */
internal fun settleTargetPx(anchorPx: Int, firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int): Int? {
    if (firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset !in 1 until anchorPx) return null
    return if (firstVisibleItemScrollOffset * 2 < anchorPx) 0 else anchorPx
}

// Matches the chip overlay and its placeholder in the list, so pinning never shifts the content
private val JobsSortChipRowHeight = 56.dp

// Also the opaque backdrop behind the pinned chips and the status bar: any transparency lets card text ghost through
private val JobsPageBackground = Color(0xFFF8F9FD)

@Composable
private fun JobsSearchInfo(state: SearchUiState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = JobFinderSpacing.lg)
            .padding(top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Query Title & Location
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (state.query.isNotBlank()) state.query else "Jobs",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.jf_ic_pin),
                    contentDescription = null,
                    tint = JobFinderColors.ActionBlue,
                    modifier = Modifier.size(16.dp)
                )
                val locationString = if (state.filters.remoteOnly) {
                    "${state.location} · Remote"
                } else {
                    "${state.location} (+${state.filters.radiusKm} km)"
                }
                Text(
                    text = locationString,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                )
            }
        }

        // Count Badge
        val countText = when {
            state.isSearching && state.totalFilteredCount == 0 -> "Suche..."
            state.isLoadingResults || !state.isResultSetComplete -> "${state.totalFilteredCount} passende Jobs bisher"
            else -> "${state.totalFilteredCount} Jobs"
        }

        Surface(
            shape = JobFinderShapes.Pill,
            color = Color(0xFFEFF6FF),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E7FF))
        ) {
            Text(
                text = countText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D4ED8),
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun JobsSortChipRow(
    state: SearchUiState,
    onSelectSortOrder: (JobSortOrder) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(JobsSortChipRowHeight)
            .drawBehind {
                // Solid under the chips, softly out at the bottom edge; invisible where only the page is behind
                drawRect(
                    Brush.verticalGradient(
                        0f to JobsPageBackground,
                        0.8f to JobsPageBackground,
                        1f to JobsPageBackground.copy(alpha = 0f)
                    )
                )
            }
            .padding(start = JobFinderSpacing.lg, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier.weight(1f).testTag("jobs_sort_chips"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(JobSortOrder.entries) { order ->
                val isSelected = state.sortOrder == order
                Surface(
                    shape = JobFinderShapes.Pill,
                    color = if (isSelected) Color(0xFF1D4ED8) else Color.White,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    shadowElevation = if (isSelected) 3.dp else 2.dp,
                    modifier = Modifier.clickable { onSelectSortOrder(order) }
                ) {
                    Text(
                        text = order.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF1E293B),
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Filter Button (48dp touch target, 34dp visual), the only one on this screen
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    role = Role.Button,
                    onClick = onFilterClick
                )
                .semantics { contentDescription = "Filter & Einstellungen" },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .shadow(if (state.clientFilters.isActive()) 3.dp else 2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (state.clientFilters.isActive()) Color(0xFF1D4ED8) else Color.White)
                    .border(1.dp, if (state.clientFilters.isActive()) Color(0xFF1D4ED8) else Color(0xFFCBD5E1), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.jf_ic_filter),
                    contentDescription = null,
                    tint = if (state.clientFilters.isActive()) Color.White else Color(0xFF334155),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}



package de.jobfinder.app.feature.home

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.compose.ui.graphics.graphicsLayer
import de.jobfinder.app.core.designsystem.JobFinderMotion
import de.jobfinder.app.core.designsystem.collectPressScale
import de.jobfinder.app.core.designsystem.components.rememberSheetCloser
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import de.jobfinder.app.core.designsystem.headerCoversStatusBar
import de.jobfinder.app.core.designsystem.StatusBarIcons
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.BorderStroke
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderColors
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.model.JobSource
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.core.model.RecentSearch
import de.jobfinder.app.core.model.SearchFilters
import de.jobfinder.app.feature.jobs.SearchUiState
import de.jobfinder.app.navigation.jobSharedBounds

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: (query: String, location: String, filters: SearchFilters) -> Unit,
    onNavigateToJobDetails: (jobId: String) -> Unit,
    modifier: Modifier = Modifier,
    activeSearchState: SearchUiState? = null,
    onNavigateToActiveSearch: () -> Unit = {},
    onNavigateToRecentSearch: (RecentSearch) -> Unit = { search ->
        onNavigateToSearch(
            search.query,
            search.location,
            SearchFilters(
                radiusKm = search.radiusKm,
                sources = search.sources,
                minSalary = search.minSalary,
                employmentTypes = search.employmentTypes,
                remoteOnly = search.isRemote,
            ),
        )
    },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = state,
        onQueryChange = { viewModel.onQueryChange(it) },
        onSearch = { query, location, filters -> onNavigateToSearch(query, location, filters) },
        onNavigateToJobDetails = onNavigateToJobDetails,
        onToggleFavorite = { viewModel.toggleFavorite(it) },
        onRemoveRecentSearch = { viewModel.removeRecentSearch(it) },
        onClearRecentSearches = { viewModel.clearRecentSearches() },
        onApplyFilters = { filters, location -> viewModel.applyFilters(filters, location) },
        activeSearchState = activeSearchState,
        onNavigateToActiveSearch = onNavigateToActiveSearch,
        onRecentSearchClick = onNavigateToRecentSearch,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: (query: String) -> Unit,
    onNavigateToJobDetails: (jobId: String) -> Unit,
    modifier: Modifier = Modifier
) = HomeScreen(
    viewModel = viewModel,
    onNavigateToSearch = { query, location, filters -> onNavigateToSearch(query) },
    onNavigateToJobDetails = onNavigateToJobDetails,
    modifier = modifier
)

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onNavigateToJobDetails: (jobId: String) -> Unit,
    onToggleFavorite: (jobId: String) -> Unit,
    onRemoveRecentSearch: (id: String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onApplyFilters: (SearchFilters) -> Unit,
    modifier: Modifier = Modifier
) = HomeScreenContent(
    state = state,
    onQueryChange = onQueryChange,
    onSearch = { query, _, _ -> onSearch(query) },
    onNavigateToJobDetails = onNavigateToJobDetails,
    onToggleFavorite = onToggleFavorite,
    onRemoveRecentSearch = onRemoveRecentSearch,
    onClearRecentSearches = onClearRecentSearches,
    onApplyFilters = { filters, _ -> onApplyFilters(filters) },
    modifier = modifier
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onQueryChange: (String) -> Unit,
    onSearch: (query: String, location: String, filters: SearchFilters) -> Unit,
    onNavigateToJobDetails: (jobId: String) -> Unit,
    onToggleFavorite: (jobId: String) -> Unit,
    onRemoveRecentSearch: (id: String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onApplyFilters: (SearchFilters, String) -> Unit,
    modifier: Modifier = Modifier,
    activeSearchState: SearchUiState? = null,
    onNavigateToActiveSearch: () -> Unit = {},
    onRecentSearchClick: (RecentSearch) -> Unit = { search ->
        onSearch(
            search.query,
            search.location,
            state.filters.copy(
                radiusKm = search.radiusKm,
                sources = search.sources,
                minSalary = search.minSalary,
                employmentTypes = search.employmentTypes,
                remoteOnly = search.isRemote,
            ),
        )
    },
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Dimming and the mock's 2 px blur follow the sheet's target, so both ease in as it opens and ease out as it starts
    // to leave. The sheet's own scrim ramps in about 190 ms and is cut off when its window closes, which read as a jerk
    // in both directions. Both values are read in draw/layer blocks, so the frames do not recompose the screen.
    val sheetShown = showFilterSheet && filterSheetState.targetValue != SheetValue.Hidden
    // Leaving is quicker than arriving; the sheet itself is gone within about 150 ms.
    val backdropMs = if (sheetShown) JobFinderMotion.MediumMs else JobFinderMotion.ReturnMs
    val backdropBlur = animateDpAsState(
        targetValue = if (sheetShown) 2.dp else 0.dp,
        animationSpec = JobFinderMotion.effects(backdropMs),
        label = "filter_backdrop_blur"
    )
    val backdropDim = animateFloatAsState(
        targetValue = if (sheetShown) FilterScrimAlpha else 0f,
        animationSpec = JobFinderMotion.effects(backdropMs),
        label = "filter_backdrop_dim"
    )

    // White status bar icons while the blue hero covers the bar, dark once the list scrolls under it
    val listState = rememberLazyListState()
    var heroPx by remember { mutableIntStateOf(0) }
    val statusTopPx = WindowInsets.statusBars.getTop(LocalDensity.current)
    val heroCoversStatusBar by remember {
        derivedStateOf {
            headerCoversStatusBar(heroPx, statusTopPx, listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
    }
    StatusBarIcons(whiteIcons = heroCoversStatusBar)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val radius = backdropBlur.value.toPx()
                    renderEffect = if (radius > 0f) BlurEffect(radius, radius, TileMode.Clamp) else null
                    clip = radius > 0f
                },
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Blue Hero Container + App Header + Search Card Overlap
            item {
                HomeHeroSection(
                    searchQuery = state.searchQuery,
                    onQueryChange = onQueryChange,
                    onSearch = { onSearch(state.searchQuery, state.location, state.filters) },
                    onOpenFilter = { showFilterSheet = true },
                    onBlueHeightChange = { heroPx = it }
                )
            }

            // Active Search Status Banner (embedded in flow, never overlays content)
            if (activeSearchState != null && (activeSearchState.isSearching || activeSearchState.isPolling)) {
                item {
                    HomeActiveSearchBanner(
                        state = activeSearchState,
                        onClick = onNavigateToActiveSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = JobFinderSpacing.lg)
                            .padding(top = 4.dp, bottom = JobFinderSpacing.sm)
                    )
                }
            }

            // Popular Jobs Carousel Section
            item {
                Spacer(modifier = Modifier.height(JobFinderSpacing.lg))
                HomePopularJobsSection(
                    jobs = state.popularJobs,
                    isLoading = state.isLoading,
                    onSeeAll = { onSearch("", state.location, state.filters) },
                    onJobClick = onNavigateToJobDetails,
                    onToggleFavorite = onToggleFavorite
                )
            }

            // Recent Searches Section
            if (state.recentSearches.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(JobFinderSpacing.xl))
                    HomeRecentSearchesSection(
                        recentSearches = state.recentSearches,
                        onSearchClick = onRecentSearchClick,
                        onRemoveSearch = onRemoveRecentSearch,
                        onClearAll = onClearRecentSearches
                    )
                }
            }
        }

        Box(Modifier.fillMaxSize().drawBehind { drawRect(Color.Black, alpha = backdropDim.value) })

        // Filter Bottom Sheet
        if (showFilterSheet) {
            HomeFilterBottomSheet(
                initialFilters = state.filters,
                initialLocation = state.location,
                sheetState = filterSheetState,
                scrimColor = Color.Transparent,
                // The sheet closes itself with its exit animation and then calls onDismiss.
                onApply = onApplyFilters,
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

/**
 * Inset-aware Blue Hero with 36dp lower curve, app header, and floating search card overlap.
 */
@Composable
private fun HomeHeroSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onOpenFilter: () -> Unit,
    onBlueHeightChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchHazeState = rememberHazeState()

    val searchGlassStyle = HazeMaterials.regular(
        containerColor = Color.White
    ).then {
        blurRadius(20.dp)
        noiseFactor(0f)
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Hellgrauer Hintergrund hinter der Suchkarte
        Box(
            modifier = Modifier
                .matchParentSize()
                .hazeSource(searchHazeState)
                .background(Color(0xFFF6F8FA))
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            // Blue Curved Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { onBlueHeightChange(it.height) }
                    .hazeSource(searchHazeState)
                    .background(
                        color = JobFinderColors.HeaderBlue,
                        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = JobFinderSpacing.lg)
                    .padding(top = JobFinderSpacing.md, bottom = 44.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // App Header: Menu Icon, JobFinder Logo with dot, Profile Icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = JobFinderSpacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Menu Button (48dp touch target)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.10f))
                                .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape)
                                .clickable(
                                    role = Role.Button,
                                    onClick = {}
                                )
                                .semantics { contentDescription = "Hauptmenü" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_grid),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Brand: White dot + JobFinder
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Text(
                                text = "JobFinder",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                        }

                        // Profile Button (48dp touch target)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.10f))
                                .border(1.dp, Color.White.copy(alpha = 0.20f), CircleShape)
                                .clickable(
                                    role = Role.Button,
                                    onClick = {}
                                )
                                .semantics { contentDescription = "Benutzerprofil" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_profile),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Headline Area: "Entdecke!" & "Finde deinen perfekten Job"
                    Spacer(modifier = Modifier.height(JobFinderSpacing.xs))
                    Text(
                        text = "Entdecke!",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 36.sp,
                            lineHeight = 40.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Finde deinen perfekten Job",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.80f),
                            fontSize = 16.sp
                        )
                    )
                }
            }

            // Floating Search Card overlapping the blue hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
                    .padding(horizontal = JobFinderSpacing.lg)
            ) {
                val cardShape = RoundedCornerShape(24.dp)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = cardShape,
                            spotColor = Color.Black.copy(alpha = 0.08f)
                        )
                        .clip(cardShape)
                        .hazeBlur(
                            input = HazeInput.Sources(searchHazeState),
                            style = searchGlassStyle,
                            performanceMode = HazePerformanceMode.Quality,
                            expandLayerBounds = true
                        ),
                    shape = cardShape,
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.60f)
                    )
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Input Row
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color(0xFFE2E8F0), JobFinderShapes.Pill),
                        shape = JobFinderShapes.Pill,
                        color = Color.White.copy(alpha = 0.80f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_search),
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onQueryChange,
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F172A)
                                ),
                                cursorBrush = SolidColor(JobFinderColors.HomeBlue),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Jobtitel, Beruf oder Stichwort",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF94A3B8)
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            // Trailing Filter Button (40dp circle)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.90f))
                                    .border(1.dp, Color.White.copy(alpha = 0.80f), CircleShape)
                                    .clickable(
                                        role = Role.Button,
                                        onClick = onOpenFilter
                                    )
                                    .semantics { contentDescription = "Filter anwenden" },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.jf_ic_filter),
                                    contentDescription = null,
                                    tint = JobFinderColors.HomeBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Main Search CTA Button (48dp height pill)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = JobFinderShapes.Pill,
                                spotColor = JobFinderColors.HomeBlue.copy(alpha = 0.25f)
                            )
                            .clip(JobFinderShapes.Pill)
                            .clickable(
                                role = Role.Button,
                                onClick = onSearch
                            ),
                        shape = JobFinderShapes.Pill,
                        color = JobFinderColors.HomeBlue
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Jobs suchen",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.jf_ic_arrow_forward),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
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
 * Horizontal Carousel for Popular Jobs with 270dp cards and alternating Blue / Coral styling.
 */
@Composable
private fun HomePopularJobsSection(
    jobs: List<JobUiModel>,
    isLoading: Boolean,
    onSeeAll: () -> Unit,
    onJobClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = JobFinderSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Beliebte Jobs",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF0F172A)
                )
            )
            Text(
                text = "Alle anzeigen",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                ),
                modifier = Modifier
                    .clip(JobFinderShapes.Pill)
                    .clickable(onClick = onSeeAll)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = JobFinderColors.HomeBlue)
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = JobFinderSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(jobs, key = { _, job -> job.id }) { index, job ->
                    HomePopularJobCard(
                        job = job,
                        index = index,
                        onClick = { onJobClick(job.id) },
                        onToggleFavorite = { onToggleFavorite(job.id) },
                        modifier = Modifier.jobSharedBounds(job.id)
                    )
                }
            }
        }
    }
}

/**
 * 270dp Popular Job Card matching Stitch mockups (Blue for even index, Coral for odd index).
 */
@Composable
private fun HomePopularJobCard(
    job: JobUiModel,
    index: Int,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = if (index % 2 == 0) JobFinderColors.HomeBlue else JobFinderColors.Coral
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale = interactionSource.collectPressScale(pressed = JobFinderMotion.PressCard)

    Surface(
        modifier = modifier
            .width(270.dp)
            .height(180.dp)
            .graphicsLayer { scaleX = pressScale.value; scaleY = pressScale.value }
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        color = cardColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Company Avatar Disc + Bookmark Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 40dp Company Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = job.companyInitials ?: "U",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = cardColor,
                            fontSize = 16.sp
                        )
                    )
                }

                // 32dp Bookmark Button (48dp touch target)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(
                            role = Role.Button,
                            onClick = onToggleFavorite
                        )
                        .semantics { contentDescription = "Job speichern" },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (job.isFavorite) R.drawable.jf_ic_bookmark_filled else R.drawable.jf_ic_bookmark
                            ),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Middle: Job Title + Company
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = job.company ?: "Unbekanntes Unternehmen",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.80f),
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Bottom Row: Portal Badge + Location with Pin
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Portal Pill
                val sourceText = when (job.sources.firstOrNull()) {
                    JobSource.STEPSTONE -> "StepStone"
                    JobSource.ARBEITSAGENTUR -> "Arbeitsagentur"
                    JobSource.KLEINANZEIGEN -> "Kleinanzeigen"
                    JobSource.LINKEDIN -> "LinkedIn"
                    JobSource.XING -> "Xing"
                    null -> "StepStone"
                }
                val extraSources = job.sources.distinct().size - 1

                Surface(
                    shape = JobFinderShapes.Pill,
                    color = Color.White.copy(alpha = 0.20f)
                ) {
                    Text(
                        text = if (extraSources > 0) "$sourceText +$extraSources" else sourceText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Location with Pin
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.jf_ic_pin),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.80f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = job.locationFormatted ?: "Berlin",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.90f),
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Active search status banner embedded in the home screen scroll flow.
 */
@Composable
private fun HomeActiveSearchBanner(
    state: SearchUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFEFF6FF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = JobFinderColors.ActionBlue,
                    strokeWidth = 2.dp
                )
                Column {
                    Text(
                        text = if (state.totalSources > 0) {
                            "Suche läuft: „${state.query.ifBlank { "Jobs" }}“ (${state.completedSources}/${state.totalSources} Portale)"
                        } else {
                            "Suche läuft: „${state.query.ifBlank { "Jobs" }}“"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${state.effectiveJobs} Stellen gefunden",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = JobFinderColors.ActionBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                    state.queryNotice?.let { notice ->
                        Text(text = notice, style = MaterialTheme.typography.bodySmall,
                            color = JobFinderColors.ActionBlue)
                    }
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.jf_ic_arrow_forward),
                contentDescription = "Zur Suche",
                tint = JobFinderColors.ActionBlue,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Recent Searches Section with Yellow / Blue variants.
 */
@Composable
private fun HomeRecentSearchesSection(
    recentSearches: List<RecentSearch>,
    onSearchClick: (RecentSearch) -> Unit,
    onRemoveSearch: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = JobFinderSpacing.lg)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Letzte Suchen",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0F172A)
                    )
                )
                Text(
                    text = "Schnell erneut starten",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                )
            }

            Text(
                text = "Löschen",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                ),
                modifier = Modifier
                    .clip(JobFinderShapes.Pill)
                    .clickable(onClick = onClearAll)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Recent Search Items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            recentSearches.forEachIndexed { index, search ->
                HomeRecentSearchPill(
                    search = search,
                    index = index,
                    onClick = { onSearchClick(search) },
                    onRemove = { onRemoveSearch(search.id) }
                )
            }
        }
    }
}

/**
 * Recent Search Pill Item matching Stitch mockups (Yellow for index 0, Blue for index 1).
 */
@Composable
private fun HomeRecentSearchPill(
    search: RecentSearch,
    index: Int,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isYellow = index % 2 == 0
    val containerBg = if (isYellow) Color(0xFFF6D886) else JobFinderColors.HomeBlue
    val textColor = if (isYellow) Color(0xFF0F172A) else Color.White
    val subtextColor = if (isYellow) Color(0xFF0F172A).copy(alpha = 0.80f) else Color.White.copy(alpha = 0.80f)
    val iconDiscBg = if (isYellow) Color.White else Color.White.copy(alpha = 0.20f)
    val iconTint = if (isYellow) Color(0xFF0F172A) else Color.White
    val closeTint = if (isYellow) Color(0xFF0F172A).copy(alpha = 0.60f) else Color.White.copy(alpha = 0.70f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = JobFinderShapes.Pill,
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .clip(JobFinderShapes.Pill)
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        shape = JobFinderShapes.Pill,
        color = containerBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon Disc + Search Query & Location
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 44dp Search Icon Disc
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconDiscBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.jf_ic_search),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = search.query,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val locationText = if (search.isRemote) {
                        "${search.location} · Remote"
                    } else if (search.radiusKm > 0) {
                        "${search.location} · ${search.radiusKm} km"
                    } else {
                        search.location
                    }
                    Text(
                        text = locationText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = subtextColor,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Remove Button (48dp touch target)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        role = Role.Button,
                        onClick = onRemove
                    )
                    .semantics { contentDescription = "Suche entfernen" },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.jf_ic_close),
                        contentDescription = null,
                        tint = closeTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private const val FilterScrimAlpha = 0.40f

/**
 * Filter Bottom Sheet matching "Filter & Einstellungen" Stitch Mockup (335 x 772 px reference).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeFilterBottomSheet(
    initialFilters: SearchFilters,
    onApply: (SearchFilters) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) = HomeFilterBottomSheet(
    initialFilters = initialFilters,
    initialLocation = "Berlin",
    onApply = { filters, _ -> onApply(filters) },
    onDismiss = onDismiss,
    modifier = modifier
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeFilterBottomSheet(
    initialFilters: SearchFilters,
    onApply: (SearchFilters, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialLocation: String = "Berlin",
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    scrimColor: Color = Color.Black.copy(alpha = FilterScrimAlpha)
) {
    var draftFilters by remember(initialFilters) { mutableStateOf(initialFilters) }
    var locationInput by remember(initialLocation) { mutableStateOf(initialLocation) }
    var showLocationDialog by remember { mutableStateOf(false) }
    // Scope lives outside the dialog branch so it survives dismissing the dialog.
    val locationScope = rememberCoroutineScope()
    val close = rememberSheetCloser(sheetState, onDismiss)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
        containerColor = Color.White,
        scrimColor = scrimColor,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 48.dp, height = 6.dp)
                    .clip(JobFinderShapes.Pill)
                    .background(Color(0xFFCBD5E1))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = JobFinderSpacing.lg)
                .padding(bottom = 36.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Row: Filter Icon Disc + "Filter & Einstellungen" + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(JobFinderColors.HomeBlue.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_filter),
                            contentDescription = null,
                            tint = JobFinderColors.HomeBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Filter & Einstellungen",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF0F172A)
                        )
                    )
                }

                // 32dp Close Button (48dp touch target)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(
                            role = Role.Button,
                            onClick = close
                        )
                        .semantics { contentDescription = "Filter schließen" },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_close),
                            contentDescription = null,
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Section: Standort (Ort)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STANDORT (ORT)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "Aktueller Standort",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = JobFinderColors.HomeBlue,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier
                            .clip(JobFinderShapes.Pill)
                            .clickable { showLocationDialog = true }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, Color(0xFFE2E8F0), JobFinderShapes.Pill),
                    shape = JobFinderShapes.Pill,
                    color = Color(0xFFF3F4F6)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_pin),
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        BasicTextField(
                            value = locationInput,
                            onValueChange = { locationInput = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            ),
                            cursorBrush = SolidColor(JobFinderColors.HomeBlue),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section: Umkreis (10 km, 25 km, 50 km, 100 km)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val currentKm = draftFilters.radiusKm

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UMKREIS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                    Surface(
                        shape = JobFinderShapes.Pill,
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = "$currentKm km aktiv",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = JobFinderColors.HomeBlue,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 25, 50, 100).forEach { km ->
                        val isSelected = currentKm == km
                        val bg = if (isSelected) JobFinderColors.HomeBlue else Color(0xFFF3F4F6)
                        val txtColor = if (isSelected) Color.White else Color(0xFF334155)

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(JobFinderShapes.Pill)
                                .clickable {
                                    draftFilters = draftFilters.copy(radiusKm = km)
                                },
                            shape = JobFinderShapes.Pill,
                            color = bg
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$km km",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = txtColor,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Section: Jobportale (StepStone, Bundesagentur für Arbeit, Kleinanzeigen, Xing)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val selectedCount = draftFilters.sources.count { it in JobSource.activeSearchSources }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "JOBPORTALE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "$selectedCount ausgewählt",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val portals = listOf(
                        Triple(JobSource.STEPSTONE, "StepStone", Color(0xFF285FCB) to Color(0xFFEFF6FF)),
                        Triple(JobSource.ARBEITSAGENTUR, "Bundesagentur für Arbeit", Color(0xFFDC2626) to Color(0xFFFEE2E2)),
                        Triple(JobSource.KLEINANZEIGEN, "Kleinanzeigen", Color(0xFF059669) to Color(0xFFD1FAE5)),
                        Triple(JobSource.XING, "Xing", Color(0xFF006567) to Color(0xFFE6F2F2)),
                        Triple(JobSource.LINKEDIN, "LinkedIn", Color(0xFF0A66C2) to Color(0xFFE8F4F9))
                    )

                    portals.forEach { (source, name, colors) ->
                        val isAvailable = source in JobSource.activeSearchSources
                        val isChecked = isAvailable && draftFilters.sources.contains(source)
                        val initial = when (source) {
                            JobSource.STEPSTONE -> "S"
                            JobSource.ARBEITSAGENTUR -> "BA"
                            JobSource.KLEINANZEIGEN -> "K"
                            JobSource.LINKEDIN -> "LI"
                            JobSource.XING -> "X"
                        }

                        val clickModifier = if (isAvailable) {
                            Modifier.clickable {
                                val activeSources = draftFilters.sources.intersect(JobSource.activeSearchSources)
                                val updated = if (isChecked) {
                                    if (activeSources.size > 1) draftFilters.sources - source else draftFilters.sources
                                } else {
                                    draftFilters.sources + source
                                }
                                draftFilters = draftFilters.copy(sources = updated)
                            }
                        } else {
                            Modifier
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, if (isAvailable) Color(0xFFE2E8F0) else Color(0xFFE2E8F0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .then(clickModifier),
                            shape = RoundedCornerShape(16.dp),
                            color = if (isAvailable) Color(0xFFF8FAFC) else Color(0xFFF1F5F9)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isAvailable) colors.second else Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initial,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isAvailable) colors.first else Color(0xFF94A3B8),
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isAvailable) Color(0xFF0F172A) else Color(0xFF94A3B8),
                                                fontSize = 14.sp
                                            )
                                        )
                                        if (!isAvailable) {
                                            Text(
                                                text = "Derzeit nicht verfügbar",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Normal,
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }

                                Checkbox(
                                    checked = isChecked,
                                    enabled = isAvailable,
                                    onCheckedChange = { checked ->
                                        if (isAvailable) {
                                            val activeSources = draftFilters.sources.intersect(JobSource.activeSearchSources)
                                            val updated = if (checked) {
                                                draftFilters.sources + source
                                            } else {
                                                if (activeSources.size > 1) draftFilters.sources - source else draftFilters.sources
                                            }
                                            draftFilters = draftFilters.copy(sources = updated)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = JobFinderColors.HomeBlue,
                                        checkmarkColor = Color.White,
                                        disabledCheckedColor = Color(0xFFCBD5E1),
                                        disabledUncheckedColor = Color(0xFFCBD5E1)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Actions Footer: Zurücksetzen & Jobs suchen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Zurücksetzen Button
                Surface(
                    onClick = {
                        draftFilters = SearchFilters()
                        locationInput = "Berlin"
                    },
                    modifier = Modifier.height(48.dp),
                    shape = JobFinderShapes.Pill,
                    color = Color(0xFFF1F5F9)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Zurücksetzen",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569),
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // Apply Button ("Filter anwenden")
                Surface(
                    onClick = {
                        onApply(draftFilters, locationInput)
                        close()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = JobFinderShapes.Pill,
                            spotColor = JobFinderColors.HomeBlue.copy(alpha = 0.25f)
                        ),
                    shape = JobFinderShapes.Pill,
                    color = JobFinderColors.HomeBlue
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter anwenden",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_arrow_forward),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (showLocationDialog) {
                val context = LocalContext.current
                AlertDialog(
                    onDismissRequest = { showLocationDialog = false },
                    title = {
                        Text(
                            text = "Standort verwenden",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        )
                    },
                    text = {
                        Text(
                            text = "Möchtest du erlauben, dass dein aktueller Standort genutzt wird?",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF475569))
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLocationDialog = false
                                locationScope.launch { locationInput = resolveDeviceLocation(context) }
                            }
                        ) {
                            Text("Akzeptieren", color = JobFinderColors.HomeBlue, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLocationDialog = false }) {
                            Text("Abbrechen", color = Color(0xFF64748B))
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Suppress("MissingPermission")
private suspend fun resolveDeviceLocation(context: Context): String = try {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    val loc = lm?.getProviders(true)?.firstNotNullOfOrNull {
        try { lm.getLastKnownLocation(it) } catch (_: SecurityException) { null }
    }
    loc?.let { reverseGeocode(context, it.latitude, it.longitude) } ?: "Berlin"
} catch (e: CancellationException) {
    throw e
} catch (_: Exception) { "Berlin" }

private suspend fun reverseGeocode(context: Context, latitude: Double, longitude: Double): String? {
    if (!Geocoder.isPresent()) return null
    return suspendCancellableCoroutine { continuation ->
        Geocoder(context, Locale.GERMANY).getFromLocation(
            latitude,
            longitude,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    continuation.resume(addresses.firstOrNull()?.let { it.locality ?: it.subAdminArea })
                }

                override fun onError(errorMessage: String?) {
                    continuation.resume(null)
                }
            }
        )
    }
}

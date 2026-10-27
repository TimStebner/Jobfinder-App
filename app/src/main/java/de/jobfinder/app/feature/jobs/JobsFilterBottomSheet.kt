package de.jobfinder.app.feature.jobs

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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.jobfinder.app.R
import de.jobfinder.app.core.designsystem.JobFinderShapes
import de.jobfinder.app.core.designsystem.components.rememberSheetCloser
import de.jobfinder.app.core.designsystem.JobFinderSpacing
import de.jobfinder.app.core.model.EmploymentType
import de.jobfinder.app.core.model.JobCategoryRegistry
import de.jobfinder.app.core.model.JobKind
import de.jobfinder.app.core.model.JobSource
import de.jobfinder.app.core.model.WorkMode
import de.jobfinder.app.core.model.WorkingTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JobsFilterBottomSheet(
    initialFilters: ClientJobFilters,
    onApply: (ClientJobFilters) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    availableCategories: Set<String> = emptySet()
) {
    var minSalaryInput by remember(initialFilters) {
        mutableStateOf(initialFilters.minSalary?.toInt()?.toString() ?: "")
    }
    var maxSalaryInput by remember(initialFilters) {
        mutableStateOf(initialFilters.maxSalary?.toInt()?.toString() ?: "")
    }
    var selectedCategories by remember(initialFilters) {
        mutableStateOf(initialFilters.categories)
    }
    var includeUnclassified by remember(initialFilters) {
        mutableStateOf(initialFilters.includeUnclassified)
    }
    var selectedWorkingTimes by remember(initialFilters) {
        mutableStateOf(initialFilters.workingTimes)
    }
    var selectedEmploymentTypes by remember(initialFilters) {
        mutableStateOf(initialFilters.employmentTypes)
    }
    var selectedJobKinds by remember(initialFilters) {
        mutableStateOf(initialFilters.jobKinds)
    }
    var selectedWorkModes by remember(initialFilters) {
        mutableStateOf(initialFilters.workModes)
    }
    var selectedSources by remember(initialFilters) {
        mutableStateOf(initialFilters.sources)
    }

    val minSalaryNum = minSalaryInput.toDoubleOrNull()
    val maxSalaryNum = maxSalaryInput.toDoubleOrNull()
    val isSalaryInvalid = minSalaryNum != null && maxSalaryNum != null && minSalaryNum > maxSalaryNum

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val close = rememberSheetCloser(sheetState, onDismiss)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
        containerColor = Color.White,
        scrimColor = Color.Black.copy(alpha = 0.40f),
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
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // Header Row: Filter Icon Disc + "Filter & Gehalt" + Close Button
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
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.jf_ic_filter),
                            contentDescription = null,
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Filter & Gehalt",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF0F172A)
                        )
                    )
                }

                // Close Button (48dp touch target)
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

            // Section: Gehaltsspanne (Jährlich Brutto)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "GEHALTSSPANNE (JÄHRLICH BRUTTO)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "Vergleichbar auf Basis von 12 Monaten (EUR brutto). Stunden- und Nettolöhne sind nicht vergleichbar.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                )

                // Quick selector pills for minimum salary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        null to "Alle",
                        30000 to "30k €",
                        50000 to "50k €",
                        70000 to "70k €",
                        90000 to "90k €"
                    ).forEach { (amt, label) ->
                        val isSelected = if (amt == null) {
                            minSalaryInput.isBlank()
                        } else {
                            minSalaryInput == amt.toString()
                        }
                        val bg = if (isSelected) Color(0xFF1D4ED8) else Color(0xFFF3F4F6)
                        val txtColor = if (isSelected) Color.White else Color(0xFF334155)

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(JobFinderShapes.Pill)
                                .clickable(
                                    role = Role.Button,
                                    onClick = { minSalaryInput = amt?.toString() ?: "" }
                                )
                                .semantics { selected = isSelected },
                            shape = JobFinderShapes.Pill,
                            color = bg
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
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

                Spacer(modifier = Modifier.height(4.dp))

                // Custom Min / Max Input Fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Min Field
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Mindestgehalt (€/Jahr)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(1.dp, if (isSalaryInvalid) Color(0xFFDC2626) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = minSalaryInput,
                                    onValueChange = { minSalaryInput = it.filter { c -> c.isDigit() } },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF0F172A)
                                    ),
                                    cursorBrush = SolidColor(Color(0xFF1D4ED8)),
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { inner ->
                                        if (minSalaryInput.isEmpty()) {
                                            Text(
                                                text = "z.B. 45000",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 14.sp
                                                )
                                            )
                                        }
                                        inner()
                                    }
                                )
                                Text(
                                    text = "€",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B)
                                    )
                                )
                            }
                        }
                    }

                    // Max Field
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Maximalgehalt (€/Jahr)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(1.dp, if (isSalaryInvalid) Color(0xFFDC2626) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = maxSalaryInput,
                                    onValueChange = { maxSalaryInput = it.filter { c -> c.isDigit() } },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF0F172A)
                                    ),
                                    cursorBrush = SolidColor(Color(0xFF1D4ED8)),
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { inner ->
                                        if (maxSalaryInput.isEmpty()) {
                                            Text(
                                                text = "Kein Limit",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 14.sp
                                                )
                                            )
                                        }
                                        inner()
                                    }
                                )
                                Text(
                                    text = "€",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B)
                                    )
                                )
                            }
                        }
                    }
                }

                if (isSalaryInvalid) {
                    Text(
                        text = "Mindestgehalt darf nicht größer als Maximalgehalt sein.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Section: Berufsfeld (Kategorien)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BERUFSFELD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Standard 18 categories
                    JobCategoryRegistry.standardCategories.forEach { (catId, displayName) ->
                        val isSelected = selectedCategories.contains(catId)
                        FilterChipPill(
                            label = displayName,
                            isSelected = isSelected,
                            onClick = {
                                selectedCategories = if (isSelected) {
                                    selectedCategories - catId
                                } else {
                                    selectedCategories + catId
                                }
                            }
                        )
                    }

                    // Extra categories from available/selected that are not in standard 18
                    val standardIds = JobCategoryRegistry.categoryDisplayNames.keys
                    val extraIds = (availableCategories + selectedCategories).filter { it !in standardIds }
                    extraIds.forEach { catId ->
                        val isSelected = selectedCategories.contains(catId)
                        FilterChipPill(
                            label = JobCategoryRegistry.displayNameFor(catId),
                            isSelected = isSelected,
                            onClick = {
                                selectedCategories = if (isSelected) {
                                    selectedCategories - catId
                                } else {
                                    selectedCategories + catId
                                }
                            }
                        )
                    }

                    // Option: Nicht klassifiziert
                    FilterChipPill(
                        label = "Nicht klassifiziert",
                        isSelected = includeUnclassified,
                        onClick = {
                            includeUnclassified = !includeUnclassified
                        }
                    )
                }
            }

            // Section: Arbeitszeit
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ARBEITSZEIT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkingTime.entries.forEach { wt ->
                        val isSelected = selectedWorkingTimes.contains(wt)
                        FilterChipPill(
                            label = wt.displayName,
                            isSelected = isSelected,
                            onClick = {
                                selectedWorkingTimes = if (isSelected) {
                                    selectedWorkingTimes - wt
                                } else {
                                    selectedWorkingTimes + wt
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("STELLENART", style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold, color = Color(0xFF64748B)))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    JobKind.entries.forEach { kind ->
                        FilterChipPill(label = kind.displayName,
                            isSelected = kind in selectedJobKinds,
                            onClick = {
                                selectedJobKinds = if (kind in selectedJobKinds) selectedJobKinds - kind
                                    else selectedJobKinds + kind
                            })
                    }
                }
            }

            // Section: Anstellungsart (eindeutig angegeben)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ANSTELLUNGSART (EINDEUTIG ANGEGEBEN)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "Nur ausdrücklich angegebene Angebote können gefiltert werden.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val distinctTypes = listOf(
                        EmploymentType.APPRENTICESHIP,
                        EmploymentType.INTERNSHIP,
                        EmploymentType.MINIJOB,
                        EmploymentType.CONTRACT
                    )
                    distinctTypes.forEach { emp ->
                        val isSelected = selectedEmploymentTypes.contains(emp)
                        FilterChipPill(
                            label = emp.displayName,
                            isSelected = isSelected,
                            onClick = {
                                selectedEmploymentTypes = if (isSelected) {
                                    selectedEmploymentTypes - emp
                                } else {
                                    selectedEmploymentTypes + emp
                                }
                            }
                        )
                    }
                }
            }

            // Section: Arbeitsmodell
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ARBEITSMODELL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkMode.entries.forEach { mode ->
                        val isSelected = selectedWorkModes.contains(mode)
                        FilterChipPill(
                            label = mode.displayName,
                            isSelected = isSelected,
                            onClick = {
                                selectedWorkModes = if (isSelected) {
                                    selectedWorkModes - mode
                                } else {
                                    selectedWorkModes + mode
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section: Quelle (Vier aktive Quellen)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "QUELLE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    JobSource.activeSearchSources.forEach { source ->
                        val isSelected = selectedSources.contains(source)
                        FilterChipPill(
                            label = source.displayName,
                            isSelected = isSelected,
                            onClick = {
                                selectedSources = if (isSelected) {
                                    selectedSources - source
                                } else {
                                    selectedSources + source
                                }
                            }
                        )
                    }
                }
            }

            // Actions Footer: Zurücksetzen & Filter anwenden
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
                        minSalaryInput = ""
                        maxSalaryInput = ""
                        selectedCategories = emptySet()
                        includeUnclassified = false
                        selectedWorkingTimes = emptySet()
                        selectedEmploymentTypes = emptySet()
                        selectedJobKinds = emptySet()
                        selectedWorkModes = emptySet()
                        selectedSources = emptySet()
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
                val isApplyEnabled = !isSalaryInvalid
                Surface(
                    onClick = {
                        if (isApplyEnabled) {
                            onApply(
                                ClientJobFilters(
                                    minSalary = minSalaryNum,
                                    maxSalary = maxSalaryNum,
                                    categories = selectedCategories,
                                    includeUnclassified = includeUnclassified,
                                    workingTimes = selectedWorkingTimes,
                                    employmentTypes = selectedEmploymentTypes,
                                    jobKinds = selectedJobKinds,
                                    workModes = selectedWorkModes,
                                    sources = selectedSources
                                )
                            )
                            close()
                        }
                    },
                    enabled = isApplyEnabled,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .shadow(
                            elevation = if (isApplyEnabled) 4.dp else 0.dp,
                            shape = JobFinderShapes.Pill,
                            spotColor = Color(0xFF1D4ED8).copy(alpha = 0.25f)
                        ),
                    shape = JobFinderShapes.Pill,
                    color = if (isApplyEnabled) Color(0xFF1D4ED8) else Color(0xFF94A3B8)
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
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Color(0xFF1D4ED8) else Color(0xFFF1F5F9)
    val txtColor = if (isSelected) Color.White else Color(0xFF334155)

    Surface(
        modifier = modifier
            .clip(JobFinderShapes.Pill)
            .clickable(
                role = Role.Checkbox,
                onClick = onClick
            )
            .semantics { selected = isSelected },
        shape = JobFinderShapes.Pill,
        color = bg
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = 44.dp)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = txtColor,
                    fontSize = 13.sp
                )
            )
        }
    }
}

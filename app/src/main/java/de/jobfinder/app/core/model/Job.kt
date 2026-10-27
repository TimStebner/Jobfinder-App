package de.jobfinder.app.core.model

enum class JobSource(val displayName: String) {
    STEPSTONE("StepStone"),
    ARBEITSAGENTUR("Bundesagentur für Arbeit"),
    KLEINANZEIGEN("Kleinanzeigen"),
    LINKEDIN("LinkedIn"),
    XING("Xing");

    companion object {
        val activeSearchSources: Set<JobSource> = setOf(
            ARBEITSAGENTUR,
            STEPSTONE,
            KLEINANZEIGEN,
            XING
        )
    }
}

fun Set<JobSource>.forActiveSearch(): Set<JobSource> =
    intersect(JobSource.activeSearchSources).ifEmpty { JobSource.activeSearchSources }


enum class EmploymentType(val displayName: String, val badgeText: String) {
    FULL_TIME("Vollzeit", "FULL TIME"),
    PART_TIME("Teilzeit", "PART TIME"),
    INTERNSHIP("Praktikum", "INTERNSHIP"),
    APPRENTICESHIP("Ausbildung", "AUSBILDUNG"),
    MINIJOB("Minijob", "MINIJOB"),
    CONTRACT("Vertrag", "CONTRACT")
}

enum class WorkingTime(val displayName: String, val badgeText: String) {
    FULL_TIME("Vollzeit", "VOLLZEIT"),
    PART_TIME("Teilzeit", "TEILZEIT")
}

enum class WorkMode(val displayName: String, val badgeText: String) {
    ONSITE("Vor Ort", "VOR ORT"),
    HYBRID("Hybrid", "HYBRID"),
    REMOTE("Remote", "REMOTE")
}

enum class DetailStatus(val quality: Int) {
    FULL(4), LIMITED(3), LISTING(1), FAILED(0), UNKNOWN(2)
}

enum class JobKind(val displayName: String) {
    APPRENTICESHIP("Ausbildung"), EMPLOYMENT("Beschäftigung"),
    INTERNSHIP("Praktikum"), DUAL_STUDY("Duales Studium"), UNKNOWN("Unbekannt")
}

enum class StepStoneFitLevel(val rawValue: String, val badgeText: String) {
    PERFECT("perfect", "Passt perfekt"),
    GOOD("good", "Passt gut"),
    POOR("poor", "Passt weniger");

    companion object {
        fun fromRaw(value: String?): StepStoneFitLevel? = when (value?.lowercase()?.trim()) {
            "perfect" -> PERFECT
            "good" -> GOOD
            "poor" -> POOR
            else -> null
        }
    }
}

data class Salary(
    val displayText: String,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val currency: String? = null,
    val period: String? = null,
    val isGross: Boolean? = null,
    val sourceText: String? = null,
    val origin: String? = null
)

fun Salary.toComparableAnnualGrossEur(): Pair<Double, Double>? {
    if (!currency.equals("EUR", ignoreCase = true)) return null
    if (isGross != true) return null
    val multiplier = when (period?.lowercase()?.trim()) {
        "year", "jahr", "annual" -> 1.0
        "month", "monat", "monthly" -> 12.0
        else -> return null
    }
    val min = minAmount?.times(multiplier)
    val max = maxAmount?.times(multiplier)
    val lower = min ?: max ?: return null
    val upper = max ?: min ?: return null
    return Pair(minOf(lower, upper), maxOf(lower, upper))
}

fun Salary?.matchesFilter(minSalary: Double?, maxSalary: Double?): Boolean {
    if (minSalary == null && maxSalary == null) return true
    if (minSalary != null && maxSalary != null && minSalary > maxSalary) return false
    val range = this?.toComparableAnnualGrossEur() ?: return false
    if (minSalary != null && range.second < minSalary) return false
    if (maxSalary != null && range.first > maxSalary) return false
    return true
}

object JobCategoryRegistry {
    val standardCategories: List<Pair<String, String>> = listOf(
        "it_software_dev" to "Softwareentwicklung",
        "it_sysadmin_infra" to "Systemadministration & Infrastruktur",
        "it_devops_cloud" to "DevOps & Cloud",
        "it_support" to "IT-Support",
        "it_cybersecurity" to "IT-Sicherheit",
        "it_data_ai" to "Daten & KI",
        "it_project_management" to "IT-Projektmanagement",
        "engineering_technical" to "Ingenieurwesen & Technik",
        "craft_manufacturing" to "Handwerk & Produktion",
        "business_admin" to "Verwaltung & Organisation",
        "finance_accounting" to "Finanzen & Rechnungswesen",
        "marketing_pr" to "Marketing & Kommunikation",
        "hr_recruiting" to "Personal & Recruiting",
        "sales_commerce" to "Vertrieb & Handel",
        "logistics_supply_chain" to "Logistik & Lieferketten",
        "healthcare_nursing" to "Gesundheit & Pflege",
        "education_social" to "Bildung & Soziales",
        "other" to "Sonstige Berufsfelder"
    )

    val categoryDisplayNames: Map<String, String> = standardCategories.toMap()

    fun displayNameFor(id: String?): String {
        if (id == null) return "Nicht klassifiziert"
        return categoryDisplayNames[id] ?: "Weitere Kategorie"
    }
}


data class Job(
    val id: String,
    val title: String,
    val company: String? = null,
    val location: String? = null,
    val postalCode: String? = null,
    val distanceKm: Double? = null,
    val postedDateText: String? = null,
    val description: String? = null,
    val detailStatus: DetailStatus = DetailStatus.UNKNOWN,
    val descriptionSource: JobSource? = null,
    val jobKind: JobKind = JobKind.UNKNOWN,
    val jobKindOrigin: String = "unknown",
    val employmentType: EmploymentType? = null,
    val workMode: WorkMode? = null,
    val salary: Salary? = null,
    val sources: List<JobSource> = emptyList(),
    val primarySource: JobSource? = null,
    val skills: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val qualifications: List<String> = emptyList(),
    val responsibilities: List<String> = emptyList(),
    val workModes: List<WorkMode> = emptyList(),
    val logoUrl: String? = null,
    val applicationUrl: String? = null,
    val sourceUrl: String? = null,
    val alternateJobIds: List<String> = emptyList(),
    val workingTime: String? = null,
    val workingTimes: Set<WorkingTime> = emptySet(),
    val primaryCategory: String? = null,
    val secondaryCategories: List<String> = emptyList(),
    val categoryConfidence: Double? = null,
    val classificationVersion: Int = 0,
    val extractionVersion: Int = 0,
    val contactName: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val stepStoneFit: StepStoneFitLevel? = null
)

data class JobUiModel(
    val id: String,
    val title: String,
    val company: String?,
    val companyInitials: String?,
    val locationFormatted: String?,
    val salaryFormatted: String?,
    val postedDateText: String?,
    val description: String?,
    val effectiveApplicationUrl: String?,
    val hasApplicationUrl: Boolean,
    val isFavorite: Boolean,
    val sources: List<JobSource>,
    val visibleSkills: List<String>,
    val skillsOverflowCount: Int,
    val employmentBadgeText: String?,
    val workModeBadgeText: String?,
    val logoUrl: String?,
    val benefits: List<String>,
    val qualifications: List<String>,
    val responsibilities: List<String> = emptyList(),
    val contactName: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val categoryLabel: String? = null,
    val workingTimeBadgeText: String? = null,
    val detailNotice: String? = null,
    val stepStoneFit: StepStoneFitLevel? = null,
    val skills: List<String> = emptyList()
)

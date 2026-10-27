package de.jobfinder.app.domain

import de.jobfinder.app.core.model.EmploymentType
import de.jobfinder.app.core.model.DetailStatus
import de.jobfinder.app.core.model.Job
import de.jobfinder.app.core.model.JobKind
import de.jobfinder.app.core.model.JobCategoryRegistry
import de.jobfinder.app.core.model.JobUiModel
import de.jobfinder.app.core.model.WorkingTime

fun Job.toUiModel(isFavorite: Boolean): JobUiModel {
    val locFormatted = when {
        location != null && distanceKm != null -> "$location · ${distanceKm.toInt()} km"
        location != null -> location
        distanceKm != null -> "${distanceKm.toInt()} km"
        else -> null
    }

    val initials = company?.let { extractInitials(it) }

    val effectiveUrl = applicationUrl ?: sourceUrl

    val maxVisibleSkills = 3
    val (visible, overflow) = if (skills.size <= maxVisibleSkills) {
        skills to 0
    } else {
        skills.take(maxVisibleSkills) to (skills.size - maxVisibleSkills)
    }

    return JobUiModel(
        id = id,
        title = title,
        company = company,
        companyInitials = initials,
        locationFormatted = locFormatted,
        salaryFormatted = salary?.let {
            if (it.origin == "portal_estimate") "${it.displayText} · Portalschätzung" else it.displayText
        },
        postedDateText = postedDateText,
        description = description,
        detailNotice = when (detailStatus) {
            DetailStatus.FULL -> null
            DetailStatus.LIMITED -> "Die Stellendetails sind begrenzt. Weitere Angaben findest du in der Originalanzeige."
            DetailStatus.LISTING -> "Dies ist eine Vorschau. Die vollständigen Angaben findest du in der Originalanzeige."
            DetailStatus.FAILED -> "Die vollständigen Details konnten nicht geladen werden. Die Originalanzeige ist weiterhin verfügbar."
            DetailStatus.UNKNOWN -> "Die Vollständigkeit dieser Stellenbeschreibung ist nicht bestätigt."
        },
        effectiveApplicationUrl = effectiveUrl,
        hasApplicationUrl = effectiveUrl != null,
        isFavorite = isFavorite,
        sources = sources,
        visibleSkills = visible,
        skillsOverflowCount = overflow,
        employmentBadgeText = when {
            jobKind != JobKind.UNKNOWN -> jobKind.displayName
            jobKindOrigin == "unknown" -> employmentType?.badgeText
            else -> null
        },
        workModeBadgeText = workMode?.badgeText,
        logoUrl = logoUrl,
        benefits = benefits,
        qualifications = qualifications,
        responsibilities = responsibilities,
        contactName = contactName,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        categoryLabel = primaryCategory?.let { JobCategoryRegistry.displayNameFor(it) },
        workingTimeBadgeText = when {
            workingTimes.contains(WorkingTime.FULL_TIME) && workingTimes.contains(WorkingTime.PART_TIME) -> "Vollzeit / Teilzeit"
            workingTimes.contains(WorkingTime.FULL_TIME) -> "Vollzeit"
            workingTimes.contains(WorkingTime.PART_TIME) -> "Teilzeit"
            else -> null
        },
        stepStoneFit = stepStoneFit,
        skills = skills
    )
}

private fun extractInitials(companyName: String): String {
    val words = companyName.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> ""
        words.size == 1 -> words[0].take(1).uppercase()
        else -> "${words[0].take(1)}${words[1].take(1)}".uppercase()
    }
}

fun parseEmploymentType(raw: String?): EmploymentType? {
    if (raw == null) return null
    val lower = raw.lowercase()
    return when {
        lower.contains("ausbildung") || lower.contains("duales studium") || lower.contains("lehrstelle") -> EmploymentType.APPRENTICESHIP
        lower.contains("praktik") || lower.contains("internship") -> EmploymentType.INTERNSHIP
        lower.contains("minijob") || lower.contains("mini-job") || lower.contains("geringfügig") -> EmploymentType.MINIJOB
        lower.contains("vertrag") || lower.contains("contract") || lower.contains("freiberuflich") -> EmploymentType.CONTRACT
        lower.contains("vollzeit") || lower.contains("full_time") || lower.contains("full-time") -> EmploymentType.FULL_TIME
        lower.contains("teilzeit") || lower.contains("part_time") || lower.contains("part-time") -> EmploymentType.PART_TIME
        else -> null
    }
}

fun parseWorkingTimes(workingTimeList: List<String>, employmentTypeStr: String?): Set<WorkingTime> {
    val result = mutableSetOf<WorkingTime>()
    val candidates = workingTimeList + listOfNotNull(employmentTypeStr)
    for (candidate in candidates) {
        val lower = candidate.lowercase()
        if (lower.contains("vollzeit") || lower.contains("full_time") || lower.contains("full-time")) {
            result.add(WorkingTime.FULL_TIME)
        }
        if (lower.contains("teilzeit") || lower.contains("part_time") || lower.contains("part-time")) {
            result.add(WorkingTime.PART_TIME)
        }
    }
    return result
}


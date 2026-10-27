package de.jobfinder.app.core.model

data class OnboardingPreferences(
    val fullTime: Boolean = true,
    val partTime: Boolean = false,
    val remote: Boolean = false
)

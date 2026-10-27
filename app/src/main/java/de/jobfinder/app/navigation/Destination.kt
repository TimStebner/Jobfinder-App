package de.jobfinder.app.navigation

sealed class Destination(val route: String) {
    data object Onboarding : Destination("onboarding")
    data object Home : Destination("home")
    data object Jobs : Destination("jobs")
    data object Favorites : Destination("favorites")

    data object JobDetails : Destination("job_details/{jobId}") {
        const val ARG_JOB_ID = "jobId"
        fun createRoute(jobId: String): String = "job_details/$jobId"
    }
}

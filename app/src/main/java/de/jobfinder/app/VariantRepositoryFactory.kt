package de.jobfinder.app

import android.content.Context
import de.jobfinder.app.domain.JobRepository

/**
 * Public frontend demo: always uses local sample data.
 * No API endpoint, auth token or backend access is required.
 */
object VariantRepositoryFactory {
    fun createJobRepository(context: Context): JobRepository = FakeJobRepository()
}

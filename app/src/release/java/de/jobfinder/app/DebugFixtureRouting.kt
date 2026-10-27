package de.jobfinder.app

import androidx.compose.runtime.Composable

object DebugFixtureRouting {
    @Composable
    fun renderFixture(fixtureId: String): Boolean {
        // Zero fixtures or mocks in release build
        return false
    }
}

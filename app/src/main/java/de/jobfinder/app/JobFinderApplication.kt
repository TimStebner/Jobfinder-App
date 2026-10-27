package de.jobfinder.app

import android.app.Application

class JobFinderApplication : Application() {
    val container: AppContainer by lazy { DefaultAppContainer(this) }
}

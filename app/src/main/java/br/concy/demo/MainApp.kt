package br.concy.demo

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import br.concy.demo.workers.CustomWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApp: Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: CustomWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(this, workManagerConfiguration)
    }
}

const val TAG = "Sensor App"
const val SHARED_PREFS = "SensorServiceState"
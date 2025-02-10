package br.concy.demo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import br.concy.demo.workers.ClearSyncedRemoteWorker
import br.concy.demo.workers.SyncRemoteWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SetupViewModel @Inject constructor(): ViewModel() {

    fun scheduleSyncRemoteWorker(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<SyncRemoteWorker>(
            repeatInterval = 5,
            TimeUnit.MINUTES
        ).setInitialDelay(1, TimeUnit.MINUTES).build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun scheduleClearSyncedRemoteWorker(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ClearSyncedRemoteWorker>(
            repeatInterval = 5,
            TimeUnit.MINUTES
        ).setInitialDelay(2, TimeUnit.MINUTES).build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
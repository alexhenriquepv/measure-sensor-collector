package br.concy.demo.workers

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import br.concy.demo.health.APIService
import br.concy.demo.model.repository.AccelRepository
import br.concy.demo.model.repository.GyroRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomWorkerFactory @Inject constructor(
    private val workerFactory: HiltWorkerFactory,
    private val accelRepository: AccelRepository,
    private val gyroRepository: GyroRepository,
    private val apiService: APIService
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ClearSyncedRemoteWorker::class.java.name -> {
                ClearSyncedRemoteWorker(
                    appContext,
                    workerParameters,
                    accelRepository,
                    gyroRepository
                )
            }
            SyncRemoteWorker::class.java.name -> {
                SyncRemoteWorker(
                    appContext,
                    workerParameters,
                    accelRepository,
                    gyroRepository,
                    apiService
                )
            }
            else -> {
                workerFactory.createWorker(appContext, workerClassName, workerParameters)
            }
        }
    }
}

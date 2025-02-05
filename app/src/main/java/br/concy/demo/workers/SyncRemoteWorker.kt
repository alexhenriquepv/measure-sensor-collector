package br.concy.demo.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.concy.demo.TAG
import br.concy.demo.health.APIService
import br.concy.demo.model.repository.AccelRepository
import br.concy.demo.model.repository.GyroRepository
import br.concy.demo.model.request.AccelRequest
import br.concy.demo.model.request.GyroRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncRemoteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val accelRepository: AccelRepository,
    private val gyroRepository: GyroRepository,
    private val apiService: APIService
) : CoroutineWorker(context, workerParameters) {

    /*
        Sent not synced registers to remote service.
        Mark this registers as synced.
     */
    private suspend fun syncAccelData() {
        val notSyncedAccel = accelRepository.getNotSynced()
        if (notSyncedAccel.isNotEmpty()) {
            Log.d(TAG, "Accel to sync: ${notSyncedAccel.size}.")

            val accelRequest = AccelRequest(
                patientId = 1,
                measurements = notSyncedAccel
            )

            val res = apiService.sendAccelData(accelRequest)
            Log.d(TAG, res.message)

            notSyncedAccel.forEach { it.sync = true }
            accelRepository.updateAll(notSyncedAccel)
        } else {
            Log.d(TAG, "Accel already synced")
        }
    }

    private suspend fun syncGyroData() {
        val notSyncedGyro = gyroRepository.getNotSynced()
        if (notSyncedGyro.isNotEmpty()) {
            Log.d(TAG, "Gyro to sync: ${notSyncedGyro.size}.")

            val gyroRequest = GyroRequest(
                patientId = 1,
                measurements = notSyncedGyro
            )

            val res = apiService.sendGyroData(gyroRequest)
            Log.d(TAG, res.message)

            notSyncedGyro.forEach { it.sync = true }
            gyroRepository.updateAll(notSyncedGyro)
        } else {
            Log.d(TAG, "Gyro already synced")
        }
    }

    override suspend fun doWork(): Result {
        try {
            syncAccelData()
            syncGyroData()
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return Result.retry()
        }
    }
}
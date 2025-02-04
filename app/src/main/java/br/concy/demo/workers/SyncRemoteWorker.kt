package br.concy.demo.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.concy.demo.TAG
import br.concy.demo.model.repository.AccelRepository
import br.concy.demo.model.repository.GyroRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncRemoteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val accelRepository: AccelRepository,
    private val gyroRepository: GyroRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        try {

            // Sync accelerometer measurements
            val notSyncedAccel = accelRepository.getNotSynced()
            Log.d(TAG, "Accel to sync: ${notSyncedAccel.size}.")

            // TODO("Sent to server")

            notSyncedAccel.forEach { it.sync = true }
            accelRepository.updateAll(notSyncedAccel)

            // Sync gyroscope measurements
            val notSyncedGyro = gyroRepository.getNotSynced()
            Log.d(TAG, "Gyro to sync: ${notSyncedGyro.size}.")

            // TODO("Sent to server")

            notSyncedGyro.forEach { it.sync = true }
            gyroRepository.updateAll(notSyncedGyro)

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return Result.retry()
        }
    }
}
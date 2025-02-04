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
class ClearSyncedRemoteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val accelRepository: AccelRepository,
    private val gyroRepository: GyroRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        try {
            // Delete synced accelerometer measurements
            val deletedCountAccel = accelRepository.deleteSynced()
            Log.d(TAG, "Accel: $deletedCountAccel synced registers was deleted.")

            // Delete synced gyroscope measurements
            val deletedCountGyro = gyroRepository.deleteSynced()
            Log.d(TAG, "Gyro: $deletedCountGyro synced registers was deleted.")

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return Result.retry()
        }
    }
}
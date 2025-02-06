package br.concy.demo.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.concy.demo.TAG
import br.concy.demo.model.repository.AccelRepository
import br.concy.demo.model.repository.GyroRepository
import br.concy.demo.model.repository.HrRepository
import br.concy.demo.model.repository.IbiRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ClearSyncedRemoteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val accelRepository: AccelRepository,
    private val gyroRepository: GyroRepository,
    private val hrRepository: HrRepository,
    private val ibiRepository: IbiRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        try {
            // Delete synced accelerometer measurements
            val deletedCountAccel = accelRepository.deleteSynced()
            Log.d(TAG, "Accel: $deletedCountAccel synced registers was deleted.")

            // Delete synced gyroscope measurements
            val deletedCountGyro = gyroRepository.deleteSynced()
            Log.d(TAG, "Gyro: $deletedCountGyro synced registers was deleted.")

            // Delete synced ibi measurements
            val deletedCountIbi = ibiRepository.deleteSynced()
            Log.d(TAG, "IBI: $deletedCountIbi synced registers was deleted.")

            // Delete synced hr measurements
            val deletedCountHr = hrRepository.deleteSynced()
            Log.d(TAG, "HR: $deletedCountHr synced registers was deleted.")

            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return Result.retry()
        }
    }
}
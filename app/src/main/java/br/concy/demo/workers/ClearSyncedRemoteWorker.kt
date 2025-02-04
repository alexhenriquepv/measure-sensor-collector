package br.concy.demo.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.concy.demo.TAG
import br.concy.demo.model.repository.AccelRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class ClearSyncedRemoteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val accelRepository: AccelRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        try {
            accelRepository.deleteSynced()
            Log.d(TAG, "Synced registers was deleted.")
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return Result.retry()
        }
    }
}
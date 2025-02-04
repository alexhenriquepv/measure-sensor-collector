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

@HiltWorker
class SyncRemoteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val accelRepository: AccelRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        try {
            val unsetData = accelRepository.getNotSynced()
            Log.d(TAG, "Total unset: ${unsetData.size}.")

            // TODO("Sent to server")

            unsetData.forEach {
                it.sync = true
            }

            accelRepository.updateAll(unsetData)
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return Result.retry()
        }
    }
}
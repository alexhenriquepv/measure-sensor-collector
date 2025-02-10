package br.concy.demo.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.concy.demo.SHARED_PREFS
import br.concy.demo.TAG
import br.concy.demo.health.APIService
import br.concy.demo.model.repository.AccelRepository
import br.concy.demo.model.repository.GyroRepository
import br.concy.demo.model.repository.HrRepository
import br.concy.demo.model.repository.IbiRepository
import br.concy.demo.model.request.AccelRequest
import br.concy.demo.model.request.GyroRequest
import br.concy.demo.model.request.HrRequest
import br.concy.demo.model.request.IbiRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncRemoteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val accelRepository: AccelRepository,
    private val gyroRepository: GyroRepository,
    private val hrRepository: HrRepository,
    private val ibiRepository: IbiRepository,
    private val apiService: APIService
) : CoroutineWorker(context, workerParameters) {

    private val prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    /*
        Sent not synced registers to remote service.
        Mark this registers as synced.
     */
    private suspend fun syncAccelData() {
        val notSyncedAccel = accelRepository.getNotSynced()
        if (notSyncedAccel.isNotEmpty()) {
            Log.d(TAG, "Accel to sync: ${notSyncedAccel.size}.")

            val accelRequest = AccelRequest(
                patientId = prefs.getInt("patientId", 0),
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
                patientId = prefs.getInt("patientId", 0),
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

    private suspend fun syncHrData() {
        val notSyncedHR = hrRepository.getNotSynced()
        if (notSyncedHR.isNotEmpty()) {
            Log.d(TAG, "HR to sync: ${notSyncedHR.size}.")

            val hrRequest = HrRequest(
                patientId = prefs.getInt("patientId", 0),
                measurements = notSyncedHR
            )

            val res = apiService.sendHrData(hrRequest)
            Log.d(TAG, res.message)

            notSyncedHR.forEach { it.sync = true }
            hrRepository.updateAll(notSyncedHR)
        } else {
            Log.d(TAG, "HR already synced")
        }
    }

    private suspend fun syncIbiData() {
        val notSyncedIbi = ibiRepository.getNotSynced()
        val patientId = prefs.getInt("patientId", 0)
        if (notSyncedIbi.isNotEmpty()) {
            Log.d(TAG, "IBI to sync: ${notSyncedIbi.size}, id: $patientId")

            val ibiRequest = IbiRequest(
                patientId = patientId,
                measurements = notSyncedIbi
            )

            val res = apiService.sendIbiData(ibiRequest)
            Log.d(TAG, "tentando fazer requisicao" )
            Log.d(TAG, res.message)

            notSyncedIbi.forEach { it.sync = true }
            ibiRepository.updateAll(notSyncedIbi)
        } else {
            Log.d(TAG, "IBI already synced")
        }
    }

    override suspend fun doWork(): Result {
        try {
            //syncAccelData()
            //syncGyroData()
            //syncHrData()
            syncIbiData()
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return Result.failure()
        }
    }
}
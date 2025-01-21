package br.concy.demo.health

import android.content.Context
import android.util.Log
import br.concy.demo.model.entity.EcgMeasurement
import br.concy.demo.model.entity.EcgRequest
import br.concy.demo.model.repository.EcgRepository
import br.concy.demo.viewmodel.HomeViewModel.Companion.TAG
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EcgManager(
    private val apiService: EcgAPIService,
    private val ecgRepository: EcgRepository,
    private val onError: (message: String) -> Unit,
    private val onServiceConnection: () -> Unit,
    private val onStartTracking: () -> Unit,
    private val onStopTracking: (itemsCount: Int) -> Unit,
    private val onSavingOnDB: () -> Unit,
    private val onSendingToRemote: () -> Unit,
) {

    private lateinit var htService: HealthTrackingService
    private lateinit var ecgTracker: HealthTracker

    private val _countdownTime = MutableStateFlow(COUNTDOWN_DEFAULT)
    private val _ecgBuffer = mutableListOf<Float>()

    val countdown = _countdownTime.asStateFlow()

    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val ecgListener = object : HealthTracker.TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            Log.d(TAG, "onDataReceived")
            if (_countdownTime.value == COUNTDOWN_DEFAULT) {
                startCountdown()
            }

            if (list.isNotEmpty()) {
                for (dp in list) {
                    val currentECG = dp.getValue(ValueKey.EcgSet.ECG_MV)
                    _ecgBuffer.add(currentECG)
                }
            }
        }

        override fun onFlushCompleted() {
            Log.e(TAG, "Flush completed")
        }

        override fun onError(err: HealthTracker.TrackerError?) {
            Log.e(TAG, "TrackerError: " + err.toString())
            onError(err.toString())
        }
    }

    fun setupSamsungConnection(context: Context) {
        val samsungConnectionListener = object : ConnectionListener {

            override fun onConnectionSuccess() {
                Log.d(TAG, "Health data service is connected")
                ecgTracker = htService.getHealthTracker(HealthTrackerType.ECG_ON_DEMAND)
                onServiceConnection()
            }

            override fun onConnectionEnded() {
                Log.d(TAG, "Health data service is disconnected")
            }

            override fun onConnectionFailed(e: HealthTrackerException) {
                Log.e(TAG, "Conn Failed reason: " + e.errorCode)
                onError("Fail on Samsung Health Connection")
            }
        }

        Log.d(TAG, "Setting up Health data service..")
        htService = HealthTrackingService(samsungConnectionListener, context)
        htService.connectService()
    }

    suspend fun saveOnDatabase() {
        Log.d(TAG, "saveOnDatabase")
        onSavingOnDB()

        val measurements = _ecgBuffer.map { item ->
            val date = Date()
            val formattedDate = sdf.format(date)

            EcgMeasurement(
                value = item,
                datetime = formattedDate
            )
        }

        ecgRepository.insertAll(measurements)

        sentToRemote()
    }

    private suspend fun sentToRemote() {
        Log.d(TAG, "sentToRemote")
        onSendingToRemote()

        try {
            val measurements = ecgRepository.getAll()

            val requestData = EcgRequest(
                patient_id = 14,
                measurements = measurements
            )

            val res = apiService.sendRegister(requestData)
            Log.d(TAG, res.message)
        } catch (e: Exception) {
            Log.e(TAG, "Fail: $e")
            onError("${e.message}")
        }

        resetSetup()
    }

    fun startCountdown() {
        Log.d(TAG, "startCountdown")
        runBlocking {
            while (_countdownTime.value > 0) {
                delay(DURATION)
                _countdownTime.value -= DURATION
            }

            stopTracking()
        }
    }

    fun startTracking() {
        if (this::ecgTracker.isInitialized) {
            Log.d(TAG, "startTracking")
            ecgTracker.setEventListener(ecgListener)
            onStartTracking()
        }
    }

    fun stopTracking() {
        Log.d(TAG, "stopTracking")
        onStopTracking(_ecgBuffer.size)
        ecgTracker.unsetEventListener()

        if (_ecgBuffer.size == 0) {
            resetSetup()
        }
    }

    fun resetSetup() {
        Log.d(TAG, "resetSetup")
        _countdownTime.value = COUNTDOWN_DEFAULT
        _ecgBuffer.clear()
        runBlocking {
            ecgRepository.deleteAll()
        }
    }

    fun isInitialized(): Boolean {
        return this::ecgTracker.isInitialized
    }

    companion object {
        const val DURATION = 1000L
        const val COUNTDOWN_DEFAULT = 30000L
    }
}
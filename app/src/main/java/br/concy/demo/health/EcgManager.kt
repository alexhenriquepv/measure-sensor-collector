package br.concy.demo.health

import android.content.Context
import android.util.Log
import br.concy.demo.model.entity.EcgMeasurement
import br.concy.demo.model.repository.EcgRepository
import br.concy.demo.model.request.EcgRequest
import br.concy.demo.model.response.EcgResponse
import br.concy.demo.viewmodel.DataCollectionViewModel.Companion.TAG
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
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
    private val onComplete: () -> Unit,
    private val scope: CoroutineScope
) {

    private lateinit var htService: HealthTrackingService
    private lateinit var ecgTracker: HealthTracker

    private val _countdownTime = MutableStateFlow(COUNTDOWN_DEFAULT)
    private val _ecgBuffer = mutableListOf<Float>()

    val countdown = _countdownTime.asStateFlow()
    private var countdownJob: Job? = null
    private var patientId = 0

    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

    private val ecgListener = object : HealthTracker.TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            Log.d(TAG, "onDataReceived")
            if (_countdownTime.value == COUNTDOWN_DEFAULT) {
                startCountdown()
            }

            if (list.isNotEmpty()) {
                val leadOff = list[0].getValue(ValueKey.EcgSet.LEAD_OFF)
                if (leadOff == 5) {
                    Log.e(TAG, "LEAD_OFF")
                } else {
                    for (dp in list) {
                        val currentECG = dp.getValue(ValueKey.EcgSet.ECG_MV)
                        _ecgBuffer.add(currentECG)
                    }
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

    fun setPatientId(id: Int) {
        patientId = id
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
                measurement = item,
                registered_at = formattedDate
            )
        }

        ecgRepository.insertAll(measurements)

        sentToRemote()
    }

    private suspend fun sentToRemote() {
        Log.d(TAG, "sentToRemote - Patient ID: $patientId")
        onSendingToRemote()

        val measurements = ecgRepository.getAll()

        val requestData = EcgRequest(
            patient_id = patientId,
            measurements = measurements
        )

        scope.launch(Dispatchers.IO) {
            try {
                val res = apiService.sendRegister(requestData)
                Log.d(TAG, res.message)
                onComplete()
            } catch (e: Exception) {
                if (e is HttpException) {
                    Log.d(TAG, e.message())
                    onError(e.message())
                }
            }

            resetSetup()
        }
    }

    fun startCountdown() {
        if (countdownJob?.isActive == true) {
            Log.d(TAG, "Countdown already running")
        } else {
            Log.d(TAG, "startCountdown")
            countdownJob = scope.launch {
                while (_countdownTime.value > 0) {
                    delay(DURATION)
                    _countdownTime.value -= DURATION
                }

                Log.d(TAG, "stopCountdown")
                stopTracking()
            }
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

        countdownJob?.cancel()
        countdownJob = null

        onStopTracking(_ecgBuffer.size)
        ecgTracker.unsetEventListener()

        if (_ecgBuffer.size == 0) {
            resetSetup()
        } else {
            Log.d(TAG, "measurements count: ${_ecgBuffer.size}")
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
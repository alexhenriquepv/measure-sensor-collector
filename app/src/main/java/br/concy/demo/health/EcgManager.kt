package br.concy.demo.health

import android.content.Context
import android.util.Log
import br.concy.demo.TAG
import br.concy.demo.model.entity.EcgMeasurement
import br.concy.demo.model.repository.EcgRepository
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EcgManager(
    private val apiService: APIService,
    private val ecgRepository: EcgRepository,
    private val onError: (message: String) -> Unit,
    private val onServiceConnection: () -> Unit,
    private val onStartTracking: () -> Unit,
    private val onStopTracking: (itemsCount: Int) -> Unit,
    private val onSavingOnDB: () -> Unit,
    private val onSendingToRemote: () -> Unit,
    private val onComplete: () -> Unit,
    private val patientId: Int
) {

    private val _countdownTime = MutableStateFlow(COUNTDOWN_DEFAULT)
    private val _electrodeActive = MutableStateFlow(false)
    private val _ecgBuffer = mutableListOf<Float>()

    val electrodeActive = _electrodeActive.asStateFlow()
    val countdown = _countdownTime.asStateFlow()
    private var countdownJob: Job? = null

    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

    private val shc = SamsungHealthConnection(
        successCallback = { onServiceConnection() },
        errorCallback = { onError("Fail on Samsung Health Connection") }
    )

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
                    _electrodeActive.value = false
                } else {
                    _electrodeActive.value = true
                    for (dp in list) {
                        val currentECG = dp.getValue(ValueKey.EcgSet.ECG_MV)
                        if (currentECG > 0) {
                            _ecgBuffer.add(currentECG)
                        }
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

    suspend fun saveOnDatabase() {
        Log.d(TAG, "saveOnDatabase")
        onSavingOnDB()

        val measurements = _ecgBuffer.map { item ->
            val date = Date()
            val formattedDate = sdf.format(date)

            EcgMeasurement(
                measurement = item,
                registeredAt = formattedDate
            )
        }

        ecgRepository.insertAll(measurements)
        sentToRemote()
    }

    private suspend fun sentToRemote() {
        Log.d(TAG, "sentToRemote - Patient ID: $patientId")
        onSendingToRemote()

        val measurements = ecgRepository.getAll()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = apiService.sendEcgData(patientId, measurements)
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
            countdownJob = CoroutineScope(Dispatchers.Main).launch {
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
        Log.d(TAG, "startTracking")
        shc.ecgTracker.setEventListener(ecgListener)
        onStartTracking()
    }

    fun stopTracking() {
        Log.d(TAG, "stopTracking")

        countdownJob?.cancel()
        countdownJob = null

        onStopTracking(_ecgBuffer.size)
        shc.ecgTracker.unsetEventListener()

        if (_ecgBuffer.size == 0) {
            resetSetup()
        } else {
            Log.d(TAG, "measurements count: ${_ecgBuffer.size}")
        }
    }

    fun startSetup(context: Context) {
        shc.connect(context)
    }

    fun resetSetup() {
        Log.d(TAG, "resetSetup")
        _countdownTime.value = COUNTDOWN_DEFAULT
        _ecgBuffer.clear()
        runBlocking {
            ecgRepository.deleteAll()
        }
    }

    companion object {
        const val DURATION = 1000L
        const val COUNTDOWN_DEFAULT = 40000L
    }
}
package br.concy.demo.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.SHARED_PREFS
import br.concy.demo.TAG
import br.concy.demo.health.APIService
import br.concy.demo.health.ShsService
import br.concy.demo.model.repository.HrRepository
import br.concy.demo.model.repository.IbiRepository
import br.concy.demo.model.request.HrRequest
import br.concy.demo.model.request.IbiRequest
import br.concy.demo.uistate.SensorsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShsViewModel @Inject constructor(
    val application: Application,
    val apiService: APIService,
    val hrRepository: HrRepository,
    val ibiRepository: IbiRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<SensorsUIState>(
        SensorsUIState.Default()
    )

    private val _timeLeft = MutableStateFlow(5 * 60 * 1000L)

    val uiState = _uiState.asStateFlow()
    val timeLeft = _timeLeft.asStateFlow()

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("SensorServiceState", Context.MODE_PRIVATE)

    fun startTracking(context: Context) {
        val intent = Intent(context, ShsService::class.java)
        context.startForegroundService(intent)
        _uiState.value = SensorsUIState.Tracking()

        viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value -= 1000
            }

            _timeLeft.value = 5 * 60 * 1000L
        }
    }

    fun stopTracking(context: Context) {
        val intent = Intent(context, ShsService::class.java)
        context.stopService(intent)
        _uiState.value = SensorsUIState.Default()
    }

    fun doUpload() {
        CoroutineScope(Dispatchers.IO).launch {
            _uiState.value = SensorsUIState.Uploading()
            syncHrData()
            delay(5000)
            syncIbiData()
            delay((5000))
            _uiState.value = SensorsUIState.Default()
        }
    }

    fun checkServiceStatus() {
        val isRunning = sharedPreferences.getBoolean("isRunning", false)
        _uiState.value = if (isRunning) SensorsUIState.Tracking() else SensorsUIState.Default()
    }

    private suspend fun syncHrData() {
        val notSyncedHR = hrRepository.getNotSynced()
        if (notSyncedHR.isNotEmpty()) {
            Log.d(TAG, "HR to sync: ${notSyncedHR.size}.")

            val hrRequest = HrRequest(
                patientId = sharedPreferences.getInt("patientId", 0),
                measurements = notSyncedHR
            )

            try {
                val res = apiService.sendHrData(hrRequest)
                Log.d(TAG, res.message)

                notSyncedHR.forEach { it.sync = true }
                hrRepository.updateAll(notSyncedHR)
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        } else {
            Log.d(TAG, "HR already synced")
        }
    }

    private suspend fun syncIbiData() {
        val notSyncedIbi = ibiRepository.getNotSynced()
        val patientId = sharedPreferences.getInt("patientId", 0)
        if (notSyncedIbi.isNotEmpty()) {
            Log.d(TAG, "IBI to sync: ${notSyncedIbi.size}, id: $patientId")

            val ibiRequest = IbiRequest(
                patientId = patientId,
                measurements = notSyncedIbi
            )

            try {
                val res = apiService.sendIbiData(ibiRequest)
                Log.d(TAG, res.message)

                notSyncedIbi.forEach { it.sync = true }
                ibiRepository.updateAll(notSyncedIbi)
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        } else {
            Log.d(TAG, "IBI already synced")
        }
    }
}
package br.concy.demo.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.TAG
import br.concy.demo.health.APIService
import br.concy.demo.health.MotionSensorsService
import br.concy.demo.model.repository.AccelRepository
import br.concy.demo.model.request.AccelRequest
import br.concy.demo.uistate.SensorsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SensorsViewModel @Inject constructor(
    application: Application,
    private val accelRepository: AccelRepository,
    private val apiService: APIService
): ViewModel() {

    private val _uiState = MutableStateFlow<SensorsUIState>(
        SensorsUIState.Default()
    )

    val uiState = _uiState.asStateFlow()

    private val prefs: SharedPreferences =
        application.getSharedPreferences("SensorServiceState", Context.MODE_PRIVATE)

    fun startTracking(context: Context) {
        val intent = Intent(context, MotionSensorsService::class.java)
        context.startForegroundService(intent)
        _uiState.value = SensorsUIState.Tracking()
    }

    fun stopTracking(context: Context) {
        val intent = Intent(context, MotionSensorsService::class.java)
        context.stopService(intent)
        _uiState.value = SensorsUIState.FinishTracking()
    }

    fun checkServiceStatus() {
        val isRunning = prefs.getBoolean("isRunning", false)
        _uiState.value = if (isRunning) SensorsUIState.Tracking() else SensorsUIState.Default()
    }

    fun sendToRemote() {
        _uiState.value = SensorsUIState.SendingToRemote()
        viewModelScope.launch(Dispatchers.IO) {
            val patientId = prefs.getInt("patientId", 0)
            val notSyncedAccel = accelRepository.getNotSynced()
            if (notSyncedAccel.isNotEmpty()) {
                try {
                    val res = apiService.sendAccelData(patientId, notSyncedAccel)

                    notSyncedAccel.forEach { it.sync = true }
                    accelRepository.updateAll(notSyncedAccel)
                    accelRepository.deleteSynced()

                    withContext(Dispatchers.Main) {
                        Log.d(TAG, res.message)
                        _uiState.value = SensorsUIState.Default()
                    }
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, ex.message.toString())
                        _uiState.value = SensorsUIState.Default()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    _uiState.value = SensorsUIState.Default()
                }
            }
        }
    }
}
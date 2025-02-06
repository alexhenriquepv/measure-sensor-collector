package br.concy.demo.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import br.concy.demo.health.ShsService
import br.concy.demo.uistate.SensorsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ShsViewModel @Inject constructor(
    application: Application
): ViewModel() {

    private val _uiState = MutableStateFlow<SensorsUIState>(
        SensorsUIState.Default()
    )

    val uiState = _uiState.asStateFlow()

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("SensorServiceState", Context.MODE_PRIVATE)

    fun startTracking(context: Context) {
        val intent = Intent(context, ShsService::class.java)
        context.startForegroundService(intent)
        _uiState.value = SensorsUIState.Tracking()
    }

    fun stopTracking(context: Context) {
        val intent = Intent(context, ShsService::class.java)
        context.stopService(intent)
        _uiState.value = SensorsUIState.Default()
    }

    fun checkServiceStatus() {
        val isRunning = sharedPreferences.getBoolean("isRunning", false)
        _uiState.value = if (isRunning) SensorsUIState.Tracking() else SensorsUIState.Default()
    }
}
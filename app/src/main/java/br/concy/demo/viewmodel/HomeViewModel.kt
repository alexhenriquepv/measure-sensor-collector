package br.concy.demo.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat.startForegroundService
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.health.MeasureDataService
import br.concy.demo.health.PassiveDataService
import br.concy.demo.model.repository.HeartRateRepository
import br.concy.demo.view.HomeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: HeartRateRepository,
    healthServicesClient: HealthServicesClient
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeUIState>(HomeUIState.Default)

    private val passiveMonitoringClient = healthServicesClient.passiveMonitoringClient
    private val measurementMonitoringClient = healthServicesClient.measureClient

    val uiState = _uiState.asStateFlow()
    val latestHeartRate = repository.lastMeasurement

    fun startCollect(ctx: Context) {
        startMeasureService(ctx)
    }

    fun stopCollect(ctx: Context) {
        stopMeasureService(ctx)
    }

    private suspend fun hasPassiveHeartRateCapability(): Boolean {
        val capabilities = passiveMonitoringClient.getCapabilitiesAsync().await()
        return (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesPassiveMonitoring)
    }

    private suspend fun hasMeasureHeartRateCapability(): Boolean {
        val capabilities = measurementMonitoringClient.getCapabilitiesAsync().await()
        return (DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure)
    }

    private fun startPassiveService() {
        viewModelScope.launch {
            if (hasMeasureHeartRateCapability()) {
                val passiveListenerConfig = PassiveListenerConfig.builder()
                    .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                    .build()

                _uiState.value = HomeUIState.Collecting

                passiveMonitoringClient.setPassiveListenerServiceAsync(
                    PassiveDataService::class.java,
                    passiveListenerConfig
                ).await()
            }
        }
    }

    private fun stopPassiveService() {
        viewModelScope.launch {
            passiveMonitoringClient.clearPassiveListenerServiceAsync().await()
            _uiState.value = HomeUIState.Default
        }
    }

    private fun startMeasureService(ctx: Context) {
        viewModelScope.launch {
            if (hasMeasureHeartRateCapability()) {
                val intent = Intent(ctx, MeasureDataService::class.java)
                startForegroundService(ctx, intent)
                _uiState.value = HomeUIState.Collecting
            }
        }
    }

    private fun stopMeasureService(ctx: Context) {
        val intent = Intent(ctx, MeasureDataService::class.java)
        ctx.stopService(intent)
        _uiState.value = HomeUIState.Default
    }
}
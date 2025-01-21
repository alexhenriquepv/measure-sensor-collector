package br.concy.demo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.health.EcgAPIService
import br.concy.demo.health.EcgManager
import br.concy.demo.model.repository.EcgRepository
import br.concy.demo.view.DataCollectionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    apiService: EcgAPIService,
    ecgRepository: EcgRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<DataCollectionUIState>(
        DataCollectionUIState.Default("Start data collect")
    )

    val uiState = _uiState.asStateFlow()

    private val ecgManager = EcgManager(
        apiService,
        ecgRepository,
        onError = { message: String ->
            _uiState.value = DataCollectionUIState.Error(message)
        },
        onServiceConnection = {
            _uiState.value = DataCollectionUIState.Default()
        },
        onStartTracking = {
            _uiState.value = DataCollectionUIState.Tracking()
        },
        onStopTracking = { itemsCount ->
            if (itemsCount > 0) {
                _uiState.value = DataCollectionUIState.StopTracking("Collected count: $itemsCount")
            } else {
                _uiState.value = DataCollectionUIState.Default()
            }
        },
        onSavingOnDB = {
            _uiState.value = DataCollectionUIState.SavingOnDB()
        },
        onSendingToRemote = {
            _uiState.value = DataCollectionUIState.SendingToRemote()
        },
        scope = viewModelScope
    )

    val countdown = ecgManager.countdown

    fun setup(context: Context, patientId: Int) {
        if (ecgManager.isInitialized()) {
            _uiState.value = DataCollectionUIState.Default()
        } else {
            ecgManager.setPatientId(patientId)
            _uiState.value = DataCollectionUIState.Setup()
            ecgManager.setupSamsungConnection(context)
        }
    }

    fun startTracking() {
        ecgManager.startTracking()
    }

    fun stopTracking() {
        ecgManager.stopTracking()
    }

    fun saveOnDatabase() {
        viewModelScope.launch {
            ecgManager.saveOnDatabase()
        }
    }

    fun resetSetup() {
        ecgManager.resetSetup()
        _uiState.value = DataCollectionUIState.Default()
    }

    companion object {
        const val TAG = "Arrhythmias"
    }
}
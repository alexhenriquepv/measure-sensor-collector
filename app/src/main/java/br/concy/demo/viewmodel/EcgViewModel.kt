package br.concy.demo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.health.APIService
import br.concy.demo.health.EcgManager
import br.concy.demo.model.repository.EcgRepository
import br.concy.demo.uistate.EcgUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EcgViewModel @Inject constructor(
    apiService: APIService,
    ecgRepository: EcgRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<EcgUIState>(
        EcgUIState.Setup()
    )

    val uiState = _uiState.asStateFlow()

    private val ecgManager = EcgManager(
        apiService,
        ecgRepository,
        onError = { message: String ->
            _uiState.value = EcgUIState.Error(message)
        },
        onServiceConnection = {
            _uiState.value = EcgUIState.Default()
        },
        onStartTracking = {
            _uiState.value = EcgUIState.Tracking()
        },
        onStopTracking = { itemsCount ->
            if (itemsCount > 0) {
                _uiState.value = EcgUIState.StopTracking("Collected count: $itemsCount")
            } else {
                _uiState.value = EcgUIState.Default()
            }
        },
        onSavingOnDB = {
            _uiState.value = EcgUIState.SavingOnDB()
        },
        onSendingToRemote = {
            _uiState.value = EcgUIState.SendingToRemote()
        },
        onComplete = {
            _uiState.value = EcgUIState.Complete("The data was sent to server.")
        },
        scope = viewModelScope
    )

    val countdown = ecgManager.countdown

    fun setup(context: Context, patientId: Int) {
        ecgManager.setPatientId(patientId)
        _uiState.value = EcgUIState.Setup()
        ecgManager.startSetup(context)
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
        _uiState.value = EcgUIState.Default()
    }
}
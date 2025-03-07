package br.concy.demo.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.SHARED_PREFS
import br.concy.demo.health.APIService
import br.concy.demo.health.EcgManager
import br.concy.demo.model.repository.EcgRepository
import br.concy.demo.uistate.EcgUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EcgViewModel @Inject constructor(
    apiService: APIService,
    ecgRepository: EcgRepository,
    application: Application
): ViewModel() {

    private val _uiState = MutableStateFlow<EcgUIState>(
        EcgUIState.Setup()
    )

    val uiState = _uiState.asStateFlow()
    private val prefs = application.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

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
        patientId = prefs.getInt("patientId", 0)
    )

    val countdown = ecgManager.countdown
    val electrodeActive = ecgManager.electrodeActive

    fun setup(context: Context) {
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
        viewModelScope.launch(Dispatchers.IO) {
            ecgManager.saveOnDatabase()
        }
    }

    fun resetSetup() {
        ecgManager.resetSetup()
        _uiState.value = EcgUIState.Default()
    }
}
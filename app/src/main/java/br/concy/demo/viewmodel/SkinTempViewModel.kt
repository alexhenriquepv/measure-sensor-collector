package br.concy.demo.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.SHARED_PREFS
import br.concy.demo.health.APIService
import br.concy.demo.health.SkinTempManager
import br.concy.demo.uistate.EcgUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkinTempViewModel @Inject constructor(
    apiService: APIService,
    application: Application
): ViewModel() {

    private val _uiState = MutableStateFlow<EcgUIState>(
        EcgUIState.Setup()
    )

    val uiState = _uiState.asStateFlow()
    private val prefs = application.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    private val skinTempManager = SkinTempManager(
        apiService,
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

    val countdown = skinTempManager.countdown

    fun setup(context: Context) {
        _uiState.value = EcgUIState.Setup()
        skinTempManager.startSetup(context)
    }

    fun startTracking() {
        skinTempManager.startTracking()
    }

    fun stopTracking() {
        skinTempManager.stopTracking()
    }

    fun saveOnDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            skinTempManager.saveOnDatabase()
        }
    }

    fun resetSetup() {
        skinTempManager.resetSetup()
        _uiState.value = EcgUIState.Default()
    }
}
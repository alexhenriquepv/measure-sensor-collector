package br.concy.demo.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.concy.demo.health.EcgAPIService
import br.concy.demo.health.EcgManager
import br.concy.demo.model.repository.EcgRepository
import br.concy.demo.view.HomeUIState
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

    private val _uiState = MutableStateFlow<HomeUIState>(
        HomeUIState.Default("Start data collect")
    )

    val uiState = _uiState.asStateFlow()

    private val ecgManager = EcgManager(
        apiService,
        ecgRepository,
        onError = { message: String ->
            _uiState.value = HomeUIState.Error(message)
        },
        onServiceConnection = {
            _uiState.value = HomeUIState.Default()
        },
        onStartTracking = {
            _uiState.value = HomeUIState.Tracking()
        },
        onStopTracking = { itemsCount ->
            if (itemsCount > 0) {
                _uiState.value = HomeUIState.StopTracking("Collected count: $itemsCount")
            } else {
                _uiState.value = HomeUIState.Default()
            }
        },
        onSavingOnDB = {
            _uiState.value = HomeUIState.SavingOnDB()
        },
        onSendingToRemote = {
            _uiState.value = HomeUIState.SendingToRemote()
        }
    )

    val countdown = ecgManager.countdown

    fun setup(context: Context) {
        if (ecgManager.isInitialized()) {
            _uiState.value = HomeUIState.Default()
        } else {
            _uiState.value = HomeUIState.Setup()
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
        _uiState.value = HomeUIState.Default()
    }

    companion object {
        const val TAG = "Arrhythmias"
    }
}
package br.concy.demo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.TAG
import br.concy.demo.health.EcgAPIService
import br.concy.demo.model.entity.Patient
import br.concy.demo.uistate.PatientSelectionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientSelectionViewModel @Inject constructor(
    private val ecgAPIService: EcgAPIService
): ViewModel() {

    private val _uiState = MutableStateFlow<PatientSelectionUIState>(
        PatientSelectionUIState.Loading()
    )

    private val _patients = MutableStateFlow<List<Patient>>(emptyList())

    val uiState = _uiState.asStateFlow()
    val patients = _patients.asStateFlow()

    init {
        getPatients()
    }

    fun getPatients() {
        _uiState.value = PatientSelectionUIState.Loading()
        viewModelScope.launch {
            try {
                val res = ecgAPIService.getPatients()
                _patients.value = res
                _uiState.value = PatientSelectionUIState.Default()
            } catch (err: Exception) {
                Log.e(TAG, err.message.toString())
                _uiState.value = PatientSelectionUIState.Error(err.message.toString())
            }
        }
    }
}
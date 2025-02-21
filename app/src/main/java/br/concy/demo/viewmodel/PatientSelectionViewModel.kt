package br.concy.demo.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.SHARED_PREFS
import br.concy.demo.TAG
import br.concy.demo.health.APIService
import br.concy.demo.model.entity.Patient
import br.concy.demo.uistate.PatientSelectionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PatientSelectionViewModel @Inject constructor(
    private val apiService: APIService,
    application: Application
): ViewModel() {

    private val _uiState = MutableStateFlow<PatientSelectionUIState>(
        PatientSelectionUIState.Loading()
    )

    private val _patients = MutableStateFlow<List<Patient>>(emptyList())

    val uiState = _uiState.asStateFlow()
    val patients = _patients.asStateFlow()

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    init {
        getPatients()
    }

    fun getPatients() {
        _uiState.value = PatientSelectionUIState.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = apiService.getPatients()
                _patients.value = res
                withContext(Dispatchers.Main) {
                    _uiState.value = PatientSelectionUIState.Default()
                }
            } catch (err: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, err.message.toString())
                    _uiState.value = PatientSelectionUIState.Error(err.message.toString())
                }
            }
        }
    }

    fun onSelectPatient(patientId: Int, callback: () -> Unit) {
        sharedPreferences.edit().putInt("patientId", patientId).apply()
        callback.invoke()
    }
}
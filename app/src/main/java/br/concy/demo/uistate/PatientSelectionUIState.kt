package br.concy.demo.uistate

sealed class PatientSelectionUIState {
    data class Default(val message: String = ""): PatientSelectionUIState()
    data class Loading(val message: String = "Loading Patients.."): PatientSelectionUIState()
    data class Error(val message: String = "Error"): PatientSelectionUIState()
}

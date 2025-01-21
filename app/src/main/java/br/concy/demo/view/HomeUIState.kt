package br.concy.demo.view

sealed class HomeUIState {
    data class Default(val message: String = "No collecting"): HomeUIState()
    data class Setup(val message: String = "Setting up service"): HomeUIState()
    data class Tracking(val message: String = "Collecting measures"): HomeUIState()
    data class StopTracking(val message: String = "Data already collected"): HomeUIState()
    data class SavingOnDB(val message: String = "Saving on Database"): HomeUIState()
    data class SendingToRemote(val message: String = "Sending to server"): HomeUIState()
    data class Error(val message: String): HomeUIState()
}

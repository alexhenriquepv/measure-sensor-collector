package br.concy.demo.uistate

sealed class EcgUIState {
    data class Default(val message: String = "No collecting"): EcgUIState()
    data class Setup(val message: String = "Setting up service"): EcgUIState()
    data class Tracking(val message: String = "Collecting measures"): EcgUIState()
    data class StopTracking(val message: String = "Data already collected"): EcgUIState()
    data class SavingOnDB(val message: String = "Saving on Database"): EcgUIState()
    data class SendingToRemote(val message: String = "Sending to server"): EcgUIState()
    data class Complete(val message: String = ""): EcgUIState()
    data class Error(val message: String): EcgUIState()
}

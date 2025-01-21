package br.concy.demo.view

sealed class DataCollectionUIState {
    data class Default(val message: String = "No collecting"): DataCollectionUIState()
    data class Setup(val message: String = "Setting up service"): DataCollectionUIState()
    data class Tracking(val message: String = "Collecting measures"): DataCollectionUIState()
    data class StopTracking(val message: String = "Data already collected"): DataCollectionUIState()
    data class SavingOnDB(val message: String = "Saving on Database"): DataCollectionUIState()
    data class SendingToRemote(val message: String = "Sending to server"): DataCollectionUIState()
    data class Error(val message: String): DataCollectionUIState()
}

package br.concy.demo.uistate

sealed class SensorsUIState {
    data class Default(val message: String = "No collecting"): SensorsUIState()
    data class Tracking(val message: String = "Collecting sensors data"): SensorsUIState()
    data class Uploading(val message: String = "Uploading data"): SensorsUIState()
}
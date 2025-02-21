package br.concy.demo.uistate

sealed class AudioRecorderUIState {
    data class Default(val message: String = "Audio recorder"): AudioRecorderUIState()
    data class Recording(val message: String = "Recording.."): AudioRecorderUIState()
    data class Recorded(val message: String = "Confirm?"): AudioRecorderUIState()
    data class Uploading(val message: String = "Uploading"): AudioRecorderUIState()
}
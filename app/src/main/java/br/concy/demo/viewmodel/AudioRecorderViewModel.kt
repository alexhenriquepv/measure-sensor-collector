package br.concy.demo.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.TAG
import br.concy.demo.health.APIService
import br.concy.demo.model.entity.AudioRecord
import br.concy.demo.uistate.AudioRecorderUIState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AudioRecorderViewModel @Inject constructor(
    val apiService: APIService,
    application: Application
): ViewModel() {

    private val _uiState = MutableStateFlow<AudioRecorderUIState>(
        AudioRecorderUIState.Default()
    )

    val uiState = _uiState.asStateFlow()

    private val filePath = "${application.externalCacheDir}/audio_recording.3gp"
    private lateinit var recorder: MediaRecorder
    private lateinit var audioFile: File

    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecord(context: Context) {

        if (!this::recorder.isInitialized) {

            audioFile = File(filePath)

            recorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFile.absolutePath)
            }
        }

        try {
            recorder.prepare()
            recorder.start()
            _uiState.value = AudioRecorderUIState.Recording()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    fun stopRecord() {
        recorder.stop()
        recorder.reset()
        recorder.release()
        _uiState.value = AudioRecorderUIState.Recorded()
    }

    fun clear() {
        _uiState.value = AudioRecorderUIState.Default()
    }

    fun sentToRemote(context: Context) {
        _uiState.value =  AudioRecorderUIState.Uploading()

        viewModelScope.launch(Dispatchers.IO) {

            val requestFile = RequestBody.create(
                MediaType.get("audio/*"),
                audioFile
            )

            val audioFormData = MultipartBody.Part.createFormData(
                "audio_file",
                audioFile.name,
                requestFile
            )

            val audioRecord = AudioRecord(0, Date())
            val bodyJson = Gson().toJson(audioRecord)

            val bodyFormData = RequestBody.create(
                MediaType.get("application/json"),
                bodyJson
            )

            try {
                apiService.uploadAudio(
                    audioFilePart = audioFormData,
                    bodyDataPart = bodyFormData
                )
                Toast.makeText(context, "File uploaded", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Fail to upload", Toast.LENGTH_LONG).show()
                }
            } finally {
                _uiState.value =  AudioRecorderUIState.Default()
            }
        }
    }
}
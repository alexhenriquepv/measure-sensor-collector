package br.concy.demo.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.concy.demo.TAG
import br.concy.demo.health.APIService
import br.concy.demo.uistate.AudioRecorderUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AudioRecorderViewModel @Inject constructor(
    val apiService: APIService,
    application: Application
): ViewModel() {

    private val _uiState = MutableStateFlow<AudioRecorderUIState>(
        AudioRecorderUIState.Default()
    )

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("SensorServiceState", Context.MODE_PRIVATE)

    val uiState = _uiState.asStateFlow()

    private val filePath = "${application.externalCacheDir}/audio_recording.3gp"
    private lateinit var recorder: MediaRecorder
    private val audioFile =  File(filePath)
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

    @RequiresApi(Build.VERSION_CODES.S)
    fun startRecord(context: Context) {

        recorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile.absolutePath)
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

            val requestFile = audioFile.asRequestBody("audio/3gp".toMediaType())

            val audioFormData = MultipartBody.Part.createFormData(
                "audio_file",
                "${Date().time}_${audioFile.name}",
                requestFile
            )
            val patientId = sharedPreferences.getInt("patientId", 0)
            //val patientId = "15".toRequestBody("text/plain".toMediaType())
            val timeBegin = sdf.format(Date()).toRequestBody("text/plain".toMediaType())

            try {
                val res = apiService.uploadAudio(
                    audioFilePart = audioFormData,
                    patientIdPart = patientId.toString().toRequestBody("text/plain".toMediaType()),
                    timeBeginPart = timeBegin
                )
                withContext(Dispatchers.Main) {
                    Log.d(TAG, res.message)
                    Toast.makeText(context, "Upload successfully", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, e.message.toString())
                    Toast.makeText(context, "Fail to upload", Toast.LENGTH_LONG).show()
                }
            } finally {
                _uiState.value =  AudioRecorderUIState.Default()
            }
        }
    }
}
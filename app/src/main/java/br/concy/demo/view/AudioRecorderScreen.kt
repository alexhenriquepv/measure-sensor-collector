package br.concy.demo.view

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import br.concy.demo.R
import br.concy.demo.uistate.AudioRecorderUIState
import br.concy.demo.viewmodel.AudioRecorderViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun AudioRecorderScreen() {

    val ctx = LocalContext.current
    val activity = ctx as Activity
    val vm: AudioRecorderViewModel = hiltViewModel()
    val uiState = vm.uiState.collectAsState()

    val text: String = when(uiState.value) {
        is AudioRecorderUIState.Default -> {
            AudioRecorderUIState.Default().message
        }
        is AudioRecorderUIState.Recording -> {
            AudioRecorderUIState.Recording().message
        }
        is AudioRecorderUIState.Recorded -> {
            AudioRecorderUIState.Recorded().message
        }
        is AudioRecorderUIState.Uploading -> {
            AudioRecorderUIState.Uploading().message
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = when(uiState.value) {
                AudioRecorderUIState.Default() -> {
                    MaterialTheme.colors.primary
                }

                AudioRecorderUIState.Recording() -> {
                    MaterialTheme.colors.secondary
                }
                else -> {
                    MaterialTheme.colors.primary
                }
            },
            fontSize = 16.sp
        )

        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            if (uiState.value is AudioRecorderUIState.Default) {
                IconButton(
                    modifier = Modifier.padding(top = 8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colors.primary
                    ),
                    onClick = {
                        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        vm.startRecord(ctx)
                    }
                )  {
                    Icon(
                        painter = painterResource(R.drawable.mic_24),
                        contentDescription = "Start Record"
                    )
                }
            }

            if (uiState.value is AudioRecorderUIState.Recording) {
                IconButton(
                    modifier = Modifier.padding(top = 8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colors.secondary,
                    ),
                    onClick = {
                        vm.stopRecord()
                    }
                )  {
                    Icon(
                        painter = painterResource(R.drawable.baseline_stop_circle_24),
                        contentDescription = "Stop Record"
                    )
                }
            }

            if (uiState.value is AudioRecorderUIState.Recorded) {
                Button(
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                    ),
                    onClick = {
                        vm.sentToRemote(ctx)
                    },
                    imageVector = Icons.Default.Done,
                    contentDescription = ""
                )

                Button(
                    modifier = Modifier.padding(top = 8.dp, start = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                    ),
                    onClick = {
                        vm.clear()
                    },
                    imageVector = Icons.Default.Delete,
                    contentDescription = ""
                )
            }

            if (uiState.value is AudioRecorderUIState.Uploading) {
                CircularProgressIndicator()
            }
        }
    }
}
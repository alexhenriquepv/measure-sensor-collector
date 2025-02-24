package br.concy.demo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import br.concy.demo.uistate.SensorsUIState
import br.concy.demo.viewmodel.SensorsViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MotionSensorsScreen(
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val vm: SensorsViewModel = hiltViewModel()
    val uiState = vm.uiState.collectAsState()

    var text = ""
    var btnText = ""

    when(uiState.value) {
        is SensorsUIState.Default -> {
            text = "Sensors Inactivated"
            btnText = "Start Service"
        }
        is SensorsUIState.Tracking -> {
            text = "Sensors Activated"
            btnText = "Stop Service"
        }
        else -> {}
    }

    LaunchedEffect(Unit) {
        vm.checkServiceStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = modifier,
            text = text,
            textAlign = TextAlign.Center,
            color = when(uiState.value) {
                is SensorsUIState.Default -> {
                    MaterialTheme.colors.primary
                }

                is SensorsUIState.Tracking -> {
                    MaterialTheme.colors.secondary
                }

                else -> MaterialTheme.colors.primary
            },
            fontSize = 16.sp
        )

        Button(
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = when(uiState.value) {
                    is SensorsUIState.Default -> {
                        MaterialTheme.colors.primary
                    }

                    is SensorsUIState.Tracking -> {
                        MaterialTheme.colors.secondary
                    }

                    else -> MaterialTheme.colors.primary
                },
            ),
            onClick = {
                when(uiState.value) {
                    is SensorsUIState.Default -> {
                        vm.startTracking(ctx)
                    }
                    is SensorsUIState.Tracking -> {
                        vm.stopTracking(ctx)
                    }
                    else -> {}
                }
            },
            imageVector = when(uiState.value) {
                is SensorsUIState.Default -> {
                    Icons.Default.PlayArrow
                }
                is SensorsUIState.Tracking -> {
                    Icons.Default.Close
                }

                else -> Icons.Default.PlayArrow
            },
            contentDescription = btnText
        )
    }
}
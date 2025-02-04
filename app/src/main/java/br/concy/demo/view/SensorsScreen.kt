package br.concy.demo.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import br.concy.demo.uistate.SensorsUIState
import br.concy.demo.viewmodel.SensorsViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SensorsScreen(
    modifier: Modifier = Modifier,
    patientId: Int,
    navController: NavController
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
    }

    LaunchedEffect(Unit) {
        vm.checkServiceStatus()
    }

    Column {
        Text(
            modifier = modifier,
            text = text,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.secondary,
            fontSize = 14.sp
        )

        Button(
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary
            ),
            onClick = {
                when(uiState.value) {
                    is SensorsUIState.Default -> {
                        vm.startTracking(ctx)
                    }
                    is SensorsUIState.Tracking -> {
                        vm.stopTracking(ctx)
                    }
                }
            },
            imageVector = Icons.Default.PlayArrow,
            contentDescription = btnText
        )
    }
}
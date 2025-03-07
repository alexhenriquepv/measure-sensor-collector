package br.concy.demo.view

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults
import br.concy.demo.uistate.EcgUIState
import br.concy.demo.viewmodel.SkinTempViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SkinTempScreen(
    modifier: Modifier = Modifier
) {

    val ctx = LocalContext.current
    val activity = ctx as Activity
    val skinTempVM: SkinTempViewModel = hiltViewModel()
    val ecgUIState = skinTempVM.uiState.collectAsState()
    val countdown = skinTempVM.countdown.collectAsState()

    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.SingleButton,
        ),
        verticalArrangement = Arrangement.Center,
    )

    LaunchedEffect(Unit) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        skinTempVM.setup(ctx)
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        columnState = listState,
    ) {

        item {

            val title = when(ecgUIState.value) {
                is EcgUIState.Setup -> (ecgUIState.value as EcgUIState.Setup).message
                is EcgUIState.Default -> "Skin Temp"
                is EcgUIState.Tracking -> "Collecting Skin Temp"
                is EcgUIState.Error -> (ecgUIState.value as EcgUIState.Error).message
                is EcgUIState.StopTracking -> (ecgUIState.value as EcgUIState.StopTracking).message
                is EcgUIState.SavingOnDB -> (ecgUIState.value as EcgUIState.SavingOnDB).message
                is EcgUIState.SendingToRemote -> (ecgUIState.value as EcgUIState.SendingToRemote).message
                is EcgUIState.Complete -> (ecgUIState.value as EcgUIState.Complete).message
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = modifier.padding(bottom = 8.dp),
                    text = title,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary
                )

                if (ecgUIState.value is EcgUIState.Tracking) {
                    Text(
                        modifier = modifier,
                        text = "${countdown.value / 1000}s",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.secondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        item {
            when (ecgUIState.value) {

                is EcgUIState.Default -> {
                    Button(
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        ),
                        onClick = {
                            skinTempVM.startTracking()
                        },
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start data collect"
                    )
                }

                is EcgUIState.Tracking -> {
                    Button(
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        ),
                        onClick = {
                            skinTempVM.stopTracking()
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop data collect"
                    )
                }

                is EcgUIState.StopTracking -> {
                    Row {
                        Button(
                            modifier = Modifier.padding(top = 8.dp, end = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error
                            ),
                            onClick = {
                                skinTempVM.resetSetup()
                            },
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Data"
                        )

                        Button(
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.secondary
                            ),
                            onClick = {
                                skinTempVM.saveOnDatabase()
                            },
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save data"
                        )
                    }
                }

                is EcgUIState.SavingOnDB -> {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize().padding(all = 1.dp),
                        strokeWidth = ProgressIndicatorDefaults.FullScreenStrokeWidth
                    )
                }

                is EcgUIState.SendingToRemote -> {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize().padding(all = 1.dp),
                        strokeWidth = ProgressIndicatorDefaults.FullScreenStrokeWidth
                    )
                }

                is EcgUIState.Error -> {
                    Chip(
                        label = "Try again",
                        colors = ChipDefaults.chipColors(backgroundColor = MaterialTheme.colors.surface),
                        onClick = {
                            skinTempVM.setup(ctx)
                        }
                    )
                }

                is EcgUIState.Complete -> {
                    Column {
                        Text(
                            modifier = modifier.padding(bottom = 8.dp),
                            text = EcgUIState.Complete().message,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }

                else -> {}
            }
        }
    }
}
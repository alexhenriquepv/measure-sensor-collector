package br.concy.demo.view

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import br.concy.demo.ui.theme.DemoTheme
import br.concy.demo.viewmodel.HomeViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun DataCollectionScreen(
    modifier: Modifier = Modifier,
    patientId: Int
) {

    val ctx = LocalContext.current
    val homeVM: HomeViewModel = hiltViewModel()
    val homeUIState = homeVM.uiState.collectAsState()
    val countdown = homeVM.countdown.collectAsState()

    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.SingleButton,
        ),
        verticalArrangement = Arrangement.Center,
    )

    LaunchedEffect(Unit) {
        homeVM.setup(ctx, patientId)
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        columnState = listState,
    ) {

        item {

            val title = when(homeUIState.value) {
                is DataCollectionUIState.Setup -> (homeUIState.value as DataCollectionUIState.Setup).message
                is DataCollectionUIState.Default -> (homeUIState.value as DataCollectionUIState.Default).message
                is DataCollectionUIState.Tracking -> (homeUIState.value as DataCollectionUIState.Tracking).message
                is DataCollectionUIState.Error -> (homeUIState.value as DataCollectionUIState.Error).message
                is DataCollectionUIState.StopTracking -> (homeUIState.value as DataCollectionUIState.StopTracking).message
                is DataCollectionUIState.SavingOnDB -> (homeUIState.value as DataCollectionUIState.SavingOnDB).message
                is DataCollectionUIState.SendingToRemote -> (homeUIState.value as DataCollectionUIState.SendingToRemote).message
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

                if (homeUIState.value is DataCollectionUIState.Tracking) {
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
            when (homeUIState.value) {

                is DataCollectionUIState.Default -> {
                    Button(
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        ),
                        onClick = {
                            homeVM.startTracking()
                        },
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start data collect"
                    )
                }

                is DataCollectionUIState.Tracking -> {
                    Button(
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        ),
                        onClick = {
                            homeVM.stopTracking()
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop data collect"
                    )
                }

                is DataCollectionUIState.StopTracking -> {
                    Row {
                        Button(
                            modifier = Modifier.padding(top = 8.dp, end = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error
                            ),
                            onClick = {
                                homeVM.resetSetup()
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
                                homeVM.saveOnDatabase()
                            },
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save data"
                        )
                    }
                }

                is DataCollectionUIState.Error -> {
                    Chip(
                        label = "Try again",
                        colors = ChipDefaults.chipColors(backgroundColor = MaterialTheme.colors.surface),
                        onClick = {
                            homeVM.setup(ctx, patientId)
                        }
                    )
                }

                else -> {}
            }
        }
    }
}
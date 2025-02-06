package br.concy.demo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ProgressIndicatorDefaults
import androidx.wear.compose.material.Text
import br.concy.demo.uistate.PatientSelectionUIState
import br.concy.demo.viewmodel.PatientSelectionViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun PatientSelectionScreen(navController: NavController) {

    val patientsVM: PatientSelectionViewModel = hiltViewModel()
    val patients = patientsVM.patients.collectAsState()
    val uiState = patientsVM.uiState.collectAsState()

    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Chip,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    )

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        columnState = listState,
    ) {
        item {
            ListHeader { Text(text = "Select a Patient", fontSize = 20.sp) }
        }

        when(uiState.value) {
            is PatientSelectionUIState.Default -> {
                items(items = patients.value) { item ->
                    Chip(
                        label = "${item.id}: ${item.name}",
                        colors = ChipDefaults.chipColors(
                            backgroundColor = MaterialTheme.colors.surface
                        ),
                        onClick = {
                            patientsVM.onSelectPatient(item.id) {
                                navController.navigate("select-service")
                            }
                        }
                    )
                }
            }
            is PatientSelectionUIState.Loading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize().padding(all = 1.dp),
                        startAngle = 295.5f,
                        endAngle = 245.5f,
                        progress = 0.3f,
                        strokeWidth = ProgressIndicatorDefaults.FullScreenStrokeWidth
                    )
                }
            }
            else -> {
                item {
                    Column {
                        Text(
                            modifier = Modifier.padding(bottom = 5.dp),
                            text = "Fail to fetch patients data.",
                            color = MaterialTheme.colors.error,
                            fontSize = 16.sp,
                        )

                        Chip(
                            label = "Try again",
                            onClick = {
                                patientsVM.getPatients()
                            },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = MaterialTheme.colors.surface
                            )
                        )

                        Chip(
                            modifier = Modifier.padding(top = 8.dp),
                            label = "Sample user",
                            colors = ChipDefaults.chipColors(
                                backgroundColor = MaterialTheme.colors.surface
                            ),
                            onClick = {
                                patientsVM.onSelectPatient(0) {
                                    navController.navigate("select-service")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
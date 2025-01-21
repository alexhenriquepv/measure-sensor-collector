package br.concy.demo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import br.concy.demo.model.entity.Patient
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun PatientSelectionScreen(navController: NavController) {
    val patients = listOf(
        Patient(id_patient = 1, name = "Rosa Clark"),
        Patient(id_patient = 2, name = "Linda Gray"),
        Patient(id_patient = 3, name = "Rob Pig"),
    )

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
        items(items = patients) { item ->
            Chip(
                label = "${item.id_patient}: ${item.name}",
                colors = ChipDefaults.chipColors(
                    backgroundColor = MaterialTheme.colors.surface
                ),
                onClick = {
                    navController.navigate("data_collection?patient_id=${item.id_patient}")
                }
            )
        }
    }
}
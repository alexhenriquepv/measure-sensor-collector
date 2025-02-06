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
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun ServiceSelectionScreen(
    navController: NavController
) {

    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Chip,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    )

    val services = ServiceType.entries

    ScalingLazyColumn(
        modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background),
        columnState = listState,
    ) {
        item {
            ListHeader { Text(text = "Select a Service", fontSize = 16.sp) }
        }

        items(items = services) { item ->
            Chip(
                label = item.serviceName,
                colors = ChipDefaults.chipColors(
                    backgroundColor = MaterialTheme.colors.surface
                ),
                onClick = {
                    when (item) {
                        ServiceType.SamsungECG -> navController.navigate("samsung-ecg")
                        ServiceType.SamsungHR -> navController.navigate("samsung-hr")
                        ServiceType.MotionSensors -> navController.navigate("motion-sensors")
                    }
                }
            )
        }
    }
}

enum class ServiceType(val serviceName: String) {
    SamsungECG("Samsung ECG"),
    SamsungHR("Samsung HR"),
    MotionSensors("Motion Sensors")
}
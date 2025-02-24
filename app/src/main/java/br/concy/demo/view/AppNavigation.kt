package br.concy.demo.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "select-patient"
    ) {
        composable("select-patient") {
            PatientSelectionScreen(navController)
        }

        composable(route = "select-service") {
            ServiceSelectionScreen(navController)
        }

        composable(route = "samsung-ecg") {
            EcgScreen()
        }

        composable(route = "samsung-hr") {
            ShsScreen()
        }

        composable(route = "motion-sensors") {
            MotionSensorsScreen()
        }

        composable(route = "audio-recorder") {
            AudioRecorderScreen()
        }
    }
}
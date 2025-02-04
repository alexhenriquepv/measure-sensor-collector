package br.concy.demo.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "select_patient"
    ) {
        composable("select_patient") {
            PatientSelectionScreen(navController)
        }

        composable(
            route = "select_service?patient_id={patient_id}",
            arguments = listOf(
                navArgument("patient_id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patient_id") ?: 0
            ServiceSelectionScreen(navController = navController, patientId = patientId)
        }

        composable(
            route = "ecg?patient_id={patient_id}",
            arguments = listOf(
                navArgument("patient_id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patient_id") ?: 0
            EcgScreen(patientId = patientId, navController = navController)
        }

        composable(
            route = "sensors?patient_id={patient_id}",
            arguments = listOf(
                navArgument("patient_id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patient_id") ?: 0
            SensorsScreen(patientId = patientId)
        }
    }
}
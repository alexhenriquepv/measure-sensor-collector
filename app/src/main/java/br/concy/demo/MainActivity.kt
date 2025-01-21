package br.concy.demo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import br.concy.demo.ui.theme.DemoTheme
import br.concy.demo.view.AppNavigation
import br.concy.demo.view.DataCollectionScreen
import br.concy.demo.view.PermissionDeniedPage
import br.concy.demo.viewmodel.PermissionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionVM =  PermissionViewModel()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionVM.checkPermission(this)

        permissionVM.hasPermission.observe(this) { isGranted ->
            setContent {
                DemoTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        if (isGranted) {
                            AppNavigation()
                        } else {
                            PermissionDeniedPage(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.mohammed.aireok

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mohammed.aireok.ui.theme.AireOKTheme
import com.mohammed.aireok.ui.theme.Navegacion

class MainActivity : ComponentActivity() {

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AireOKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Navegacion(
                        modifier = Modifier,
                        onNavControllerReady = { navController = it }
                    )
                }
            }
        }
    }

    // Llamado cuando la app ya está abierta (singleTask) y llega un deep link
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.handleDeepLink(intent)
    }
}

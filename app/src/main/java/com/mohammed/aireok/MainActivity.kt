package com.mohammed.aireok

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mohammed.aireok.presentation.navigation.Navegacion
import com.mohammed.aireok.presentation.navigation.Pantalla
import com.mohammed.aireok.ui.theme.AireOKTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavController? = null
    private var pendingResetToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Arranque en frío desde deep link (app no estaba en memoria)
        if (savedInstanceState == null) {
            val data = intent.data
            if (data?.scheme == "aireok" && data.host == "reset-password") {
                pendingResetToken = data.getQueryParameter("token") ?: ""
            }
        }
        enableEdgeToEdge()
        setContent {
            AireOKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Navegacion(
                        modifier = Modifier,
                        onNavControllerReady = { nc ->
                            navController = nc
                            val token = pendingResetToken
                            if (token != null) {
                                pendingResetToken = null
                                // Si NavHost ya manejó el deep link, currentDestination
                                // apuntará a reset-password; en ese caso no navegar de nuevo
                                if (nc.currentDestination?.route?.startsWith("reset-password") != true) {
                                    nc.navigate("reset-password?token=${Uri.encode(token)}") {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // App ya en memoria: el sistema llama onNewIntent en lugar de onCreate
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val data = intent.data
        if (data?.scheme == "aireok" && data.host == "reset-password") {
            val token = data.getQueryParameter("token") ?: ""
            navController?.navigate("reset-password?token=${Uri.encode(token)}") {
                launchSingleTop = true
                popUpTo(Pantalla.Login.ruta) { inclusive = false }
            }
        } else {
            navController?.handleDeepLink(intent)
        }
    }
}

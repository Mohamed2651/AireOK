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
        val data = intent.data
        if (data?.scheme == "aireok" && data.host == "reset-password") {
            pendingResetToken = data.getQueryParameter("token")?.takeIf { it.isNotEmpty() }
        }
        enableEdgeToEdge()
        setContent {
            AireOKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    Navegacion(
                        modifier = Modifier,
                        pendingResetToken = pendingResetToken,
                        onNavControllerReady = { nc -> navController = nc }
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

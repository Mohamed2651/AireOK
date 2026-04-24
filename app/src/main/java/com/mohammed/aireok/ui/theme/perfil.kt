package com.mohammed.aireok.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PantallaPerfil(navController: NavController, userViewModel: UserViewModel) {
    var mostrarCerrarSesion by remember { mutableStateOf(false) }

    if (mostrarCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarCerrarSesion = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Quieres cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarCerrarSesion = false
                    userViewModel.logout {
                        navController.navigate(Pantalla.Login.ruta) { popUpTo(0) { inclusive = true } }
                    }
                }) { Text("Sí", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCerrarSesion = false }) { Text("Cancelar") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Avatar con gradiente de marca (consistente en ambos modos)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF0D47A1), greenBlue)))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userViewModel.usuarioNombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(userViewModel.usuarioNombre.ifBlank { "Usuario" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                if (userViewModel.usuarioEmail.isNotBlank()) {
                    Text(userViewModel.usuarioEmail, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Información de cuenta",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PerfilInfoCard(Icons.Filled.Person, "Nombre", userViewModel.usuarioNombre.ifBlank { "—" })
        Spacer(Modifier.height(8.dp))
        PerfilInfoCard(Icons.Filled.Email, "Email", userViewModel.usuarioEmail.ifBlank { "—" })

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { mostrarCerrarSesion = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Filled.Logout, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PerfilInfoCard(icono: ImageVector, etiqueta: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(etiqueta, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(valor, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

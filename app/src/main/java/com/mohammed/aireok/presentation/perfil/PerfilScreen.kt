package com.mohammed.aireok.presentation.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mohammed.aireok.presentation.navigation.Pantalla
import com.mohammed.aireok.ui.theme.greenBlue

@Composable
fun PerfilScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    var modoEdicion by remember { mutableStateOf(false) }
    var nombreEditar by remember { mutableStateOf("") }
    var emailEditar by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.perfilState) {
        if (viewModel.perfilState is PerfilUiState.Success) {
            modoEdicion = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
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
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(viewModel.nombre.ifBlank { "Usuario" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                if (viewModel.email.isNotBlank()) {
                    Text(viewModel.email, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (modoEdicion) {
            Text("Editar perfil", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp))

            val colores = OutlinedTextFieldDefaults.colors(
                focusedBorderColor        = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor      = MaterialTheme.colorScheme.outline,
                focusedLabelColor         = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor       = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLeadingIconColor   = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor               = MaterialTheme.colorScheme.primary,
                focusedTextColor          = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor        = MaterialTheme.colorScheme.onSurface,
            )

            OutlinedTextField(
                value = nombreEditar, onValueChange = { nombreEditar = it },
                label = { Text("Nombre") }, leadingIcon = { Icon(Icons.Filled.Person, null) },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp), colors = colores
            )

            Spacer(Modifier.height(12.dp))

            val emailValido = emailEditar.trim().isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditar.trim()).matches()

            OutlinedTextField(
                value = emailEditar, onValueChange = { emailEditar = it },
                label = { Text("Email") }, leadingIcon = { Icon(Icons.Filled.Email, null) },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp), colors = colores,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailEditar.isNotBlank() && !emailValido,
                supportingText = {
                    if (emailEditar.isNotBlank() && !emailValido)
                        Text("Introduce un email válido", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                }
            )

            if (viewModel.perfilState is PerfilUiState.Error) {
                Text(
                    text = (viewModel.perfilState as PerfilUiState.Error).message,
                    color = MaterialTheme.colorScheme.error, fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { modoEdicion = false; viewModel.resetPerfilState() },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                ) { Text("Cancelar") }
                Button(
                    onClick = { viewModel.actualizarPerfil(nombreEditar, emailEditar.trim()) },
                    enabled = nombreEditar.isNotBlank() && emailValido && viewModel.perfilState !is PerfilUiState.Loading,
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                ) {
                    if (viewModel.perfilState is PerfilUiState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Guardar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Información de cuenta", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                IconButton(
                    onClick = { nombreEditar = viewModel.nombre; emailEditar = viewModel.email; viewModel.resetPerfilState(); modoEdicion = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Filled.Edit, "Editar perfil", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
            }

            PerfilInfoCard(Icons.Filled.Person, "Nombre", viewModel.nombre.ifBlank { "—" })
            Spacer(Modifier.height(8.dp))
            PerfilInfoCard(Icons.Filled.Email, "Email", viewModel.email.ifBlank { "—" })
            Spacer(Modifier.height(8.dp))
            PerfilAccionCard(icono = Icons.Filled.Lock, etiqueta = "Cambiar contraseña") {
                navController.navigate(Pantalla.RecuperarPassword.ruta)
            }
        }
    }
}

@Composable
private fun PerfilInfoCard(icono: ImageVector, etiqueta: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
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

@Composable
private fun PerfilAccionCard(icono: ImageVector, etiqueta: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Text(etiqueta, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

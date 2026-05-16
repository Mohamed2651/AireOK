package com.mohammed.aireok.presentation.perfil

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mohammed.aireok.ui.theme.green
import com.mohammed.aireok.ui.theme.greenBlue

@Composable
fun CambiarPasswordScreen(
    navController: NavController,
    viewModel: CambiarPasswordViewModel = hiltViewModel()
) {
    var passwordActual by remember { mutableStateOf("") }
    var passwordNueva by remember { mutableStateOf("") }
    var passwordConfirmar by remember { mutableStateOf("") }
    var actualVisible by remember { mutableStateOf(false) }
    var nuevaVisible by remember { mutableStateOf(false) }
    var confirmarVisible by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState
    val isDark = isSystemInDarkTheme()
    val gradientStart = if (isDark) Color(0xFF07255C) else Color(0xFF0D47A1)
    val gradientEnd   = if (isDark) Color(0xFF00433A) else greenBlue

    val passwordsCoinciden = passwordNueva == passwordConfirmar
    val formValido = passwordActual.isNotBlank() && passwordNueva.length >= 6 &&
        passwordsCoinciden && uiState !is CambiarPasswordUiState.Loading

    DisposableEffect(Unit) { onDispose { viewModel.resetState() } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(gradientStart, gradientEnd)))
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).padding(top = 16.dp, start = 8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(88.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.LockReset, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Cambiar contraseña",
                color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                "Introduce tu contraseña actual y elige una nueva",
                color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp, textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState is CambiarPasswordUiState.Success) {
                        SuccessPassword(onVolver = { navController.popBackStack() })
                    } else {
                        FormularioPassword(
                            passwordActual = passwordActual,
                            onActualChange = { passwordActual = it },
                            actualVisible = actualVisible,
                            onToggleActual = { actualVisible = !actualVisible },
                            passwordNueva = passwordNueva,
                            onNuevaChange = { passwordNueva = it },
                            nuevaVisible = nuevaVisible,
                            onToggleNueva = { nuevaVisible = !nuevaVisible },
                            passwordConfirmar = passwordConfirmar,
                            onConfirmarChange = { passwordConfirmar = it },
                            confirmarVisible = confirmarVisible,
                            onToggleConfirmar = { confirmarVisible = !confirmarVisible },
                            passwordsCoinciden = passwordsCoinciden,
                            formValido = formValido,
                            uiState = uiState,
                            onGuardar = { viewModel.cambiarPassword(passwordActual, passwordNueva) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SuccessPassword(onVolver: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.CheckCircle, null, tint = green, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "¡Contraseña actualizada!",
            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Tu contraseña se ha cambiado correctamente.",
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Volver al perfil", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun FormularioPassword(
    passwordActual: String,
    onActualChange: (String) -> Unit,
    actualVisible: Boolean,
    onToggleActual: () -> Unit,
    passwordNueva: String,
    onNuevaChange: (String) -> Unit,
    nuevaVisible: Boolean,
    onToggleNueva: () -> Unit,
    passwordConfirmar: String,
    onConfirmarChange: (String) -> Unit,
    confirmarVisible: Boolean,
    onToggleConfirmar: () -> Unit,
    passwordsCoinciden: Boolean,
    formValido: Boolean,
    uiState: CambiarPasswordUiState,
    onGuardar: () -> Unit
) {
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

    Text(
        "Cambiar contraseña",
        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(Modifier.height(20.dp))

    OutlinedTextField(
        value = passwordActual,
        onValueChange = onActualChange,
        label = { Text("Contraseña actual", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
        visualTransformation = if (actualVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleActual) {
                Icon(
                    if (actualVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = colores
    )

    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = passwordNueva,
        onValueChange = onNuevaChange,
        label = { Text("Nueva contraseña", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
        visualTransformation = if (nuevaVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleNueva) {
                Icon(
                    if (nuevaVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = true,
        isError = passwordNueva.isNotEmpty() && passwordNueva.length < 6,
        supportingText = {
            if (passwordNueva.isNotEmpty() && passwordNueva.length < 6)
                Text("Mínimo 6 caracteres", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = colores
    )

    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = passwordConfirmar,
        onValueChange = onConfirmarChange,
        label = { Text("Confirmar contraseña", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
        visualTransformation = if (confirmarVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleConfirmar) {
                Icon(
                    if (confirmarVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = true,
        isError = passwordConfirmar.isNotEmpty() && !passwordsCoinciden,
        supportingText = {
            if (passwordConfirmar.isNotEmpty() && !passwordsCoinciden)
                Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = colores
    )

    AnimatedVisibility(visible = uiState is CambiarPasswordUiState.Error, enter = fadeIn(), exit = fadeOut()) {
        Text(
            text = (uiState as? CambiarPasswordUiState.Error)?.message ?: "",
            color = MaterialTheme.colorScheme.error, fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }

    Spacer(Modifier.height(20.dp))

    Button(
        onClick = onGuardar,
        enabled = formValido,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
    ) {
        if (uiState is CambiarPasswordUiState.Loading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp
            )
        } else {
            Text("Cambiar contraseña", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

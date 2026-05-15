package com.mohammed.aireok.presentation.auth.resetPassword

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
import androidx.compose.material.icons.outlined.Key
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
import com.mohammed.aireok.presentation.navigation.Pantalla
import com.mohammed.aireok.ui.theme.green
import com.mohammed.aireok.ui.theme.greenBlue

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    var tokenManual by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarVisible by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState
    val isDark = isSystemInDarkTheme()

    val gradientStart = if (isDark) Color(0xFF07255C) else Color(0xFF0D47A1)
    val gradientEnd   = if (isDark) Color(0xFF00433A) else greenBlue

    val tokenDesdeDeepLink = viewModel.tokenFromDeepLink.isNotBlank()
    val tokenEfectivo = if (tokenDesdeDeepLink) viewModel.tokenFromDeepLink else tokenManual
    val passwordsCoinciden = password == confirmar
    val formValido = tokenEfectivo.isNotBlank() && password.length >= 6 && passwordsCoinciden

    DisposableEffect(Unit) {
        onDispose { viewModel.resetState() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(gradientStart, gradientEnd)))
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
            Text("Nueva contraseña", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text(
                if (tokenDesdeDeepLink) "Token recibido · establece tu nueva contraseña"
                else "Pega el token del correo y establece tu nueva contraseña",
                color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp, textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (uiState is ResetPasswordUiState.Success) {
                        ConfirmacionReset(
                            onIrAlLogin = {
                                navController.navigate(Pantalla.Login.ruta) { popUpTo(0) { inclusive = true } }
                            }
                        )
                    } else {
                        FormularioReset(
                            tokenDesdeDeepLink = tokenDesdeDeepLink,
                            tokenManual = tokenManual,
                            onTokenManualChange = { tokenManual = it },
                            password = password,
                            onPasswordChange = { password = it },
                            passwordVisible = passwordVisible,
                            onTogglePasswordVisible = { passwordVisible = !passwordVisible },
                            confirmar = confirmar,
                            onConfirmarChange = { confirmar = it },
                            confirmarVisible = confirmarVisible,
                            onToggleConfirmarVisible = { confirmarVisible = !confirmarVisible },
                            passwordsCoinciden = passwordsCoinciden,
                            formValido = formValido,
                            uiState = uiState,
                            onCambiar = { viewModel.resetPassword(tokenEfectivo.trim(), password) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ConfirmacionReset(onIrAlLogin: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.CheckCircle, null, tint = green, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text("¡Contraseña actualizada!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(10.dp))
        Text(
            "Tu contraseña se ha cambiado correctamente.\nYa puedes iniciar sesión con la nueva contraseña.",
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, lineHeight = 20.sp
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onIrAlLogin, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Ir al inicio de sesión", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun FormularioReset(
    tokenDesdeDeepLink: Boolean,
    tokenManual: String,
    onTokenManualChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisible: () -> Unit,
    confirmar: String,
    onConfirmarChange: (String) -> Unit,
    confirmarVisible: Boolean,
    onToggleConfirmarVisible: () -> Unit,
    passwordsCoinciden: Boolean,
    formValido: Boolean,
    uiState: ResetPasswordUiState,
    onCambiar: () -> Unit
) {
    val coloresInput = OutlinedTextFieldDefaults.colors(
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

    Text("Restablecer contraseña", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(20.dp))

    if (!tokenDesdeDeepLink) {
        OutlinedTextField(
            value = tokenManual,
            onValueChange = onTokenManualChange,
            label = { Text("Token del correo") },
            leadingIcon = { Icon(Icons.Outlined.Key, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = coloresInput,
            supportingText = {
                Text("Copia y pega el token del email de recuperación", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        )
        Spacer(Modifier.height(12.dp))
    }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Nueva contraseña", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisible) {
                Icon(if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        singleLine = true,
        isError = password.isNotEmpty() && password.length < 6,
        supportingText = {
            if (password.isNotEmpty() && password.length < 6)
                Text("Mínimo 6 caracteres", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = coloresInput
    )

    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = confirmar,
        onValueChange = onConfirmarChange,
        label = { Text("Confirmar contraseña", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
        visualTransformation = if (confirmarVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleConfirmarVisible) {
                Icon(if (confirmarVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        singleLine = true,
        isError = confirmar.isNotEmpty() && !passwordsCoinciden,
        supportingText = {
            if (confirmar.isNotEmpty() && !passwordsCoinciden)
                Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = coloresInput
    )

    AnimatedVisibility(visible = uiState is ResetPasswordUiState.Error, enter = fadeIn(), exit = fadeOut()) {
        Text(
            text = (uiState as? ResetPasswordUiState.Error)?.message ?: "",
            color = MaterialTheme.colorScheme.error,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }

    Spacer(Modifier.height(20.dp))

    Button(
        onClick = onCambiar,
        enabled = formValido && uiState !is ResetPasswordUiState.Loading,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
    ) {
        if (uiState is ResetPasswordUiState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
        } else {
            Text("Cambiar contraseña", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

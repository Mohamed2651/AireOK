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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mohammed.aireok.ui.theme.green
import com.mohammed.aireok.ui.theme.greenBlue

@Composable
fun CambiarEmailScreen(
    navController: NavController,
    viewModel: CambiarEmailViewModel = hiltViewModel()
) {
    var nuevoEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState
    val isDark = isSystemInDarkTheme()
    val gradientStart = if (isDark) Color(0xFF07255C) else Color(0xFF0D47A1)
    val gradientEnd   = if (isDark) Color(0xFF00433A) else greenBlue

    val emailValido = nuevoEmail.isNotBlank() &&
        android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail.trim()).matches()
    val formValido = emailValido && password.isNotBlank() && uiState !is CambiarEmailUiState.Loading

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
                Icon(Icons.Filled.Email, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Cambiar email",
                color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                "Introduce tu nuevo email y confirma con tu contraseña",
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
                    if (uiState is CambiarEmailUiState.Success) {
                        SuccessEmail(onVolver = { navController.popBackStack() })
                    } else {
                        FormularioEmail(
                            nuevoEmail = nuevoEmail,
                            onEmailChange = { nuevoEmail = it },
                            password = password,
                            onPasswordChange = { password = it },
                            passwordVisible = passwordVisible,
                            onTogglePasswordVisible = { passwordVisible = !passwordVisible },
                            emailValido = emailValido,
                            formValido = formValido,
                            uiState = uiState,
                            onGuardar = { viewModel.cambiarEmail(nuevoEmail, password) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SuccessEmail(onVolver: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.CheckCircle, null, tint = green, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "¡Email actualizado!",
            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Tu email se ha cambiado correctamente.",
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
private fun FormularioEmail(
    nuevoEmail: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisible: () -> Unit,
    emailValido: Boolean,
    formValido: Boolean,
    uiState: CambiarEmailUiState,
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
        "Nuevo email",
        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(Modifier.height(20.dp))

    OutlinedTextField(
        value = nuevoEmail,
        onValueChange = onEmailChange,
        label = { Text("Nuevo email") },
        leadingIcon = { Icon(Icons.Outlined.Email, null) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = colores,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = nuevoEmail.isNotBlank() && !emailValido,
        supportingText = {
            if (nuevoEmail.isNotBlank() && !emailValido)
                Text("Introduce un email válido", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        }
    )

    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Contraseña actual") },
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisible) {
                Icon(
                    if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = colores
    )

    AnimatedVisibility(visible = uiState is CambiarEmailUiState.Error, enter = fadeIn(), exit = fadeOut()) {
        Text(
            text = (uiState as? CambiarEmailUiState.Error)?.message ?: "",
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
        if (uiState is CambiarEmailUiState.Loading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp
            )
        } else {
            Text("Guardar email", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

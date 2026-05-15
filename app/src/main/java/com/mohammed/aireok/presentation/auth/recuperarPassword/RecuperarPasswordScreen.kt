package com.mohammed.aireok.presentation.auth.recuperarPassword

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
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mohammed.aireok.presentation.navigation.Pantalla
import com.mohammed.aireok.ui.theme.green
import com.mohammed.aireok.ui.theme.greenBlue

@Composable
fun RecuperarPasswordScreen(
    navController: NavController,
    viewModel: RecuperarPasswordViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val uiState = viewModel.uiState
    val isDark = isSystemInDarkTheme()

    val gradientStart = if (isDark) Color(0xFF07255C) else Color(0xFF0D47A1)
    val gradientEnd   = if (isDark) Color(0xFF00433A) else greenBlue

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
                Icon(Icons.Filled.MarkEmailRead, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text("Recuperar contraseña", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text("Te enviaremos un correo con instrucciones", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp, textAlign = TextAlign.Center)

            Spacer(Modifier.height(36.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (uiState is RecuperarPasswordUiState.Success) {
                        ConfirmacionEnvio(onVolver = {
                            navController.navigate(Pantalla.Login.ruta) { popUpTo(0) { inclusive = true } }
                        })
                    } else {
                        FormularioRecuperar(
                            email = email,
                            onEmailChange = { email = it },
                            uiState = uiState,
                            onEnviar = { viewModel.recuperarPassword(email) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ConfirmacionEnvio(onVolver: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.CheckCircle, null, tint = green, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text("Correo enviado", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(10.dp))
        Text(
            "Si el email existe recibirás un correo en breve.\nRevisa también la carpeta de spam.",
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, lineHeight = 20.sp
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Volver al inicio de sesión", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun FormularioRecuperar(
    email: String,
    onEmailChange: (String) -> Unit,
    uiState: RecuperarPasswordUiState,
    onEnviar: () -> Unit
) {
    val emailValido = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val coloresInput = OutlinedTextFieldDefaults.colors(
        focusedBorderColor    = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor  = MaterialTheme.colorScheme.outline,
        focusedLabelColor     = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor   = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLeadingIconColor   = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor           = MaterialTheme.colorScheme.primary,
        focusedTextColor      = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor    = MaterialTheme.colorScheme.onSurface,
    )

    Text("Introduce tu email", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    Text("Te enviaremos un enlace para restablecer tu contraseña", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp), textAlign = TextAlign.Center)

    Spacer(Modifier.height(24.dp))

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        leadingIcon = {
            Icon(Icons.Outlined.Email, null,
                tint = if (emailValido || email.isEmpty()) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.error)
        },
        singleLine = true,
        isError = email.isNotEmpty() && !emailValido,
        supportingText = {
            if (email.isNotEmpty() && !emailValido)
                Text("Introduce un email válido", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = coloresInput
    )

    AnimatedVisibility(visible = uiState is RecuperarPasswordUiState.Error, enter = fadeIn(), exit = fadeOut()) {
        Text(
            text = (uiState as? RecuperarPasswordUiState.Error)?.message ?: "",
            color = MaterialTheme.colorScheme.error,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }

    Spacer(Modifier.height(20.dp))

    Button(
        onClick = onEnviar,
        enabled = emailValido && uiState !is RecuperarPasswordUiState.Loading,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
    ) {
        if (uiState is RecuperarPasswordUiState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
        } else {
            Text("Enviar enlace", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

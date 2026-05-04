package com.mohammed.aireok.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
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
import androidx.navigation.NavController

private fun esEmailValido(email: String) =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

@Composable
fun PantallaLogin(navController: NavController, userViewModel: UserViewModel) {
    var passwordHidden by remember { mutableStateOf(true) }
    val authState = userViewModel.authState
    val emailValido = esEmailValido(userViewModel.email)
    val isDark = isSystemInDarkTheme()

    val gradientStart = if (isDark) Color(0xFF07255C) else Color(0xFF0D47A1)
    val gradientEnd   = if (isDark) Color(0xFF00433A) else greenBlue

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Pantalla.Home.ruta) {
                popUpTo(Pantalla.Login.ruta) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(gradientStart, gradientEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Air, null, tint = Color.White, modifier = Modifier.size(52.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text("AireOK", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Text("Calidad del aire en tiempo real", color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp)

            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Bienvenido",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Inicia sesión para continuar",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(Modifier.height(24.dp))

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

                    OutlinedTextField(
                        value = userViewModel.email,
                        onValueChange = { userViewModel.actualizarEmail(it) },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email, null,
                                tint = if (emailValido || userViewModel.email.isEmpty())
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        },
                        singleLine = true,
                        isError = userViewModel.email.isNotEmpty() && !emailValido,
                        supportingText = {
                            if (userViewModel.email.isNotEmpty() && !emailValido)
                                Text("Introduce un email válido", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresInput
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = userViewModel.password,
                        onValueChange = { userViewModel.actualizarPassword(it) },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                Icon(
                                    if (passwordHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresInput
                    )

                    AnimatedVisibility(visible = authState is AuthState.Error, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = (authState as? AuthState.Error)?.message ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { userViewModel.login {} },
                        enabled = emailValido && userViewModel.password.isNotBlank() && authState !is AuthState.Loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                        } else {
                            Text("Iniciar sesión", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "¿Olvidaste tu contraseña?",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(Pantalla.RecuperarPassword.ruta)
                        }
                    )

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿No tienes cuenta?  ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Text(
                            "Regístrate",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                userViewModel.limpiarEstado()
                                navController.navigate(Pantalla.Registro.ruta)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

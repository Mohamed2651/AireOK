package com.mohammed.aireok.presentation.auth.registro

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mohammed.aireok.presentation.navigation.Pantalla

@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: RegistroViewModel = hiltViewModel()
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val uiState = viewModel.uiState
    val isDark = isSystemInDarkTheme()

    val gradientStart = if (isDark) Color(0xFF0A2E0D) else Color(0xFF1B5E20)
    val gradientEnd   = if (isDark) Color(0xFF07255C) else Color(0xFF0D47A1)

    val passwordsMatch = viewModel.password == confirmPassword || confirmPassword.isEmpty()
    val emailValido = android.util.Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches()
    val formularioValido = viewModel.nombre.isNotBlank()
            && emailValido
            && viewModel.password.isNotBlank()
            && confirmPassword.isNotBlank()
            && viewModel.password == confirmPassword
            && uiState !is RegistroUiState.Loading

    LaunchedEffect(uiState) {
        if (uiState is RegistroUiState.Success) {
            navController.navigate(Pantalla.Login.ruta) {
                popUpTo(Pantalla.Registro.ruta) { inclusive = true }
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    viewModel.resetState()
                    navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Air, null, tint = Color.White, modifier = Modifier.size(34.dp))
                }
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(48.dp))
            }

            Spacer(Modifier.height(12.dp))
            Text("Crear cuenta", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text("Únete a AireOK", color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp)

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
                        value = viewModel.nombre,
                        onValueChange = { viewModel.updateNombre(it) },
                        label = { Text("Nombre") },
                        leadingIcon = { Icon(Icons.Outlined.Person, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresInput
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Outlined.Email, null) },
                        singleLine = true,
                        isError = viewModel.email.isNotEmpty() && !emailValido,
                        supportingText = {
                            if (viewModel.email.isNotEmpty() && !emailValido)
                                Text("Introduce un email válido", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresInput
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = { Text("Contraseña", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                Icon(
                                    if (passwordHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresInput
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                Icon(
                                    if (passwordHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    null, tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        singleLine = true,
                        isError = !passwordsMatch,
                        supportingText = {
                            if (!passwordsMatch) Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresInput
                    )

                    AnimatedVisibility(visible = uiState is RegistroUiState.Error, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = (uiState as? RegistroUiState.Error)?.message ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.registro() },
                        enabled = formularioValido,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                    ) {
                        if (uiState is RegistroUiState.Loading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                        } else {
                            Text("Crear cuenta", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("¿Ya tienes cuenta?  ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Text(
                            "Inicia sesión",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                viewModel.resetState()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

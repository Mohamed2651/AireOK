package com.mohammed.aireok.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable

fun PantallaLogin(navController: NavController, userViewModel: UserViewModel) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    val formulariovalido = userViewModel.nombre.isNotBlank() && userViewModel.password.isNotBlank()

    var mensaje by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Pantalla de Login")
        Spacer(Modifier.height(16.dp))
        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = userViewModel.nombre,
                onValueChange = { userViewModel.actualizarNombre(it)},
                label = { Text("Nombre:") },
                singleLine = true,  // evita que el usuario haga "Enter" para crear nuevas líneas
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = userViewModel.password,
                onValueChange = {userViewModel.actualizarPassword(it)  },
                label = { Text("Contraseña") },

                visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,

                trailingIcon = {
                    val imagen = if (passwordHidden)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff


                    val descripcion = if (passwordHidden) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        Icon(imageVector = imagen, contentDescription = descripcion)
                    }
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
            )
        }
        // cuando se inicia sesión se borra la pantalla de login para que el usuario no vuelva
        val context = LocalContext.current
        Button(onClick = {

            val esValido = DBTest.validarUsuario(context, userViewModel.nombre, userViewModel.password)
             if (esValido) {
                 navController.navigate(Pantalla.Home.ruta) {
                     popUpTo(Pantalla.Login.ruta) { inclusive = false }
                     launchSingleTop = true}
            } else {
                mensaje= "Usuario o contraseña incorrectos"
            }
        }
            // si el usuario no rellena los dos campos no puede iniciar sesión
        , enabled = formulariovalido, modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth() ) {
            Text("Iniciar sesión")
        }
        Text(text = mensaje, modifier = Modifier.padding(20.dp))
    }
}


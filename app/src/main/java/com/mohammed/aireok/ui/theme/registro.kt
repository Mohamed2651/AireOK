package com.mohammed.aireok.ui.theme

import DataBaseCopier.insertarUsuario
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PantallaRegistro(navController: NavController, userViewModel: UserViewModel) {

    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var confirmpassword by rememberSaveable { mutableStateOf("") }
    var mensaje by rememberSaveable { mutableStateOf("") }

    var formulariovalido= userViewModel.nombre.isNotBlank() &&
            userViewModel.password.isNotBlank() &&
            confirmpassword.isNotBlank() &&
            userViewModel.email.isNotBlank()


    Column(modifier = Modifier.padding(20.dp)) {
        Text("Registro")
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
        OutlinedTextField(
            value = confirmpassword,
            onValueChange = { confirmpassword = it },
            label = { Text("Confirmar contraseña") },

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

        OutlinedTextField(
            value = userViewModel.email,
            onValueChange = { userViewModel.actualizarEmail(it)},
            label = { Text("Email:") },
            singleLine = true,  // evita que el usuario haga "Enter" para crear nuevas líneas
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        )
        val context = LocalContext.current

        Button(onClick = {

            val esValido = userViewModel.password == confirmpassword
            if (esValido) {
                insertarUsuario(context, userViewModel.nombre, userViewModel.email, userViewModel.password)
                userViewModel.actualizarNombre("")
                userViewModel.actualizarPassword("")
                navController.navigate(Pantalla.Login.ruta) {
                    popUpTo(Pantalla.Login.ruta) { inclusive = false }
                    launchSingleTop = true}
            } else {
                mensaje= "Las contraseñas no coinciden"
            }
        }
            // si el usuario no rellena los dos campos no puede iniciar sesión
            , enabled = formulariovalido, modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth() ) {
            Text("Registrarse")
        }
        Text(text = mensaje, modifier = Modifier.padding(20.dp))

    }
}
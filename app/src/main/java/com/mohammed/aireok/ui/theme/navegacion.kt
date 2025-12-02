package com.mohammed.aireok.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Pantalla(val ruta: String) {
    object Login : Pantalla("login")
    object Home : Pantalla("home")
    object Registro: Pantalla("registro")
    object Modificar: Pantalla("modificar")
}

@Composable
fun Navegacion(modifier: Modifier) {

    val navController = rememberNavController()
    val  userViewModel: UserViewModel = viewModel()

    NavHost(navController, startDestination = Pantalla.Login.ruta) {

        composable(Pantalla.Login.ruta) {
            PantallaLogin(navController, userViewModel)
        }
        composable(Pantalla.Home.ruta) {
            PantallaHome(navController, userViewModel)
        }

        composable (Pantalla.Registro.ruta ) {
            PantallaRegistro(navController, userViewModel)
        }
        /*
        composable(Pantalla.Modificar.ruta) {
            ModificarDatos(navController)
        }*/
    }
}
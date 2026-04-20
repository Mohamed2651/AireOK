package com.mohammed.aireok.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Pantalla(val ruta: String) {
    object Login : Pantalla("login")
    object Home : Pantalla("home")
    object Registro : Pantalla("registro")
    object Estacion : Pantalla("estacion/{uid}") {
        fun conUid(uid: String) = "estacion/$uid"
    }
}

@Composable
fun Navegacion(modifier: Modifier) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    NavHost(navController, startDestination = Pantalla.Login.ruta) {
        composable(Pantalla.Login.ruta) {
            PantallaLogin(navController, userViewModel)
        }
        composable(Pantalla.Home.ruta) {
            PantallaHome(navController, userViewModel)
        }
        composable(Pantalla.Registro.ruta) {
            PantallaRegistro(navController, userViewModel)
        }
        composable(
            route = Pantalla.Estacion.ruta,
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
            PantallaEstacion(navController, uid)
        }
    }
}

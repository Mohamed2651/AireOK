package com.mohammed.aireok.presentation.navigation

sealed class Pantalla(val ruta: String) {
    object Splash : Pantalla("splash")
    object Login : Pantalla("login")
    object Home : Pantalla("home")
    object Registro : Pantalla("registro")
    object Buscar : Pantalla("buscar")
    object Mapa : Pantalla("mapa")
    object Perfil : Pantalla("perfil")
    object Consejos : Pantalla("consejos")
    object Favoritos : Pantalla("favoritos")
    object Estacion : Pantalla("estacion/{uid}") {
        fun conUid(uid: String) = "estacion/$uid"
    }
    object RecuperarPassword : Pantalla("recuperar-password")
    object ResetPassword : Pantalla("reset-password")
}

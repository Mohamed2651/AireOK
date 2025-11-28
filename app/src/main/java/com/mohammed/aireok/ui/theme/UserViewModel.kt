package com.mohammed.aireok.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class UserViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // Datos del usuario logueado
    var id by mutableStateOf(savedStateHandle["id"] ?: 0)
        private set
    var nombre by mutableStateOf(savedStateHandle["nombre"] ?: "")
        private set
    var password by mutableStateOf(savedStateHandle["password"] ?: "")
        private  set
    var email by mutableStateOf(savedStateHandle["email"] ?: "")
        private set
    var rol by mutableStateOf(savedStateHandle["rol"] ?: "")
        private set

    // Actualización de valores
    fun actualizarNombre(valor: String) {
        nombre = valor
        savedStateHandle["nombre"] = valor
    }
    fun actualizarPassword(valor: String) {
        password = valor
        savedStateHandle["password"] = valor
    }
    fun actualizarEmail(valor: String) {
        email = valor
        savedStateHandle["email"] = valor
    }
    fun actualizarRol(valor: String) {
        rol = valor
        savedStateHandle["rol"] = valor
    }

    // Limpiar datos al cerrar sesión
    fun limpiarUsuario() {
        id = 0
        nombre = ""
        email = ""
        rol = ""

        savedStateHandle.remove<Int>("id")
        savedStateHandle.remove<String>("nombre")
        savedStateHandle.remove<String>("email")
        savedStateHandle.remove<String>("rol")
    }
}

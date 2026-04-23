package com.mohammed.aireok.ui.theme

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.data.TokenManager
import com.mohammed.aireok.network.AuthHolder
import com.mohammed.aireok.network.EstacionBusquedaResponse
import com.mohammed.aireok.network.EstacionResponse
import com.mohammed.aireok.network.LoginRequest
import com.mohammed.aireok.network.RegistroRequest
import com.mohammed.aireok.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val nombre: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)

    var email by mutableStateOf("")
        private set
    var nombre by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    var authState: AuthState by mutableStateOf(AuthState.Idle)
        private set

    var usuarioNombre by mutableStateOf("")
        private set
    var usuarioEmail by mutableStateOf("")
        private set

    var estaciones: List<EstacionResponse> by mutableStateOf(emptyList())
        private set
    var cargandoEstaciones by mutableStateOf(false)
        private set
    var errorEstaciones: String? by mutableStateOf(null)
        private set

    fun actualizarEmail(valor: String) { email = valor }
    fun actualizarNombre(valor: String) { nombre = valor }
    fun actualizarPassword(valor: String) { password = valor }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = RetrofitClient.api.login(LoginRequest(email, password))
                tokenManager.saveSession(response.token, response.usuario.nombre, response.usuario.email)
                AuthHolder.token = response.token
                usuarioNombre = response.usuario.nombre
                usuarioEmail = response.usuario.email
                authState = AuthState.Success(response.usuario.nombre)
                onSuccess()
            } catch (e: HttpException) {
                val code = e.code()
                authState = AuthState.Error(
                    if (code == 401 || code == 400) "Email o contraseña incorrectos"
                    else "Error del servidor ($code)"
                )
            } catch (e: Exception) {
                authState = AuthState.Error("Sin conexión. Inténtalo de nuevo.")
            }
        }
    }

    fun registro(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                RetrofitClient.api.registro(RegistroRequest(nombre, email, password))
                authState = AuthState.Idle
                onSuccess()
            } catch (e: HttpException) {
                val code = e.code()
                authState = AuthState.Error(
                    if (code == 409) "Este email ya está registrado"
                    else if (code == 400) "Datos inválidos"
                    else "Error del servidor ($code)"
                )
            } catch (e: Exception) {
                authState = AuthState.Error("Sin conexión. Inténtalo de nuevo.")
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearSession()
            AuthHolder.token = ""
            usuarioNombre = ""
            usuarioEmail = ""
            email = ""
            password = ""
            nombre = ""
            authState = AuthState.Idle
            onSuccess()
        }
    }

    fun limpiarEstado() {
        authState = AuthState.Idle
        email = ""
        password = ""
        nombre = ""
    }

    var todasEstaciones: List<EstacionResponse> by mutableStateOf(emptyList())
        private set
    var cargandoMapa by mutableStateOf(false)
        private set

    fun cargarEstacionesMapa() {
        if (cargandoMapa || todasEstaciones.isNotEmpty()) return
        viewModelScope.launch {
            cargandoMapa = true
            try {
                todasEstaciones = RetrofitClient.api.estaciones()
            } catch (e: Exception) {
                // silently ignored — map stays empty
            } finally {
                cargandoMapa = false
            }
        }
    }

    var resultadosBusqueda: List<EstacionBusquedaResponse> by mutableStateOf(emptyList())
        private set
    var cargandoBusqueda by mutableStateOf(false)
        private set
    var errorBusqueda: String? by mutableStateOf(null)
        private set
    var icasBusqueda: Map<Int, Int?> by mutableStateOf(emptyMap())
        private set

    fun buscarEstaciones(query: String) {
        if (query.isBlank()) { resultadosBusqueda = emptyList(); return }
        viewModelScope.launch {
            cargandoBusqueda = true
            errorBusqueda = null
            icasBusqueda = emptyMap()
            try {
                val resultados = RetrofitClient.api.buscarEstacion(query)
                resultadosBusqueda = resultados
                // Fetch real ICA for each result in parallel
                resultados.forEach { estacion ->
                    val uid = estacion.uid ?: return@forEach
                    launch {
                        try {
                            val detalle = RetrofitClient.api.estacion(uid.toString())
                            icasBusqueda = icasBusqueda + (uid to detalle.ica)
                        } catch (_: Exception) {}
                    }
                }
            } catch (e: Exception) {
                errorBusqueda = "No se encontraron resultados"
                resultadosBusqueda = emptyList()
            } finally {
                cargandoBusqueda = false
            }
        }
    }

    fun limpiarBusqueda() {
        resultadosBusqueda = emptyList()
        errorBusqueda = null
        icasBusqueda = emptyMap()
    }

    fun cargarEstaciones() {
        if (cargandoEstaciones || estaciones.isNotEmpty()) return
        viewModelScope.launch {
            cargandoEstaciones = true
            errorEstaciones = null
            try {
                estaciones = RetrofitClient.api.estaciones().take(12)
            } catch (e: Exception) {
                errorEstaciones = "No se pudieron cargar las estaciones"
            } finally {
                cargandoEstaciones = false
            }
        }
    }
}

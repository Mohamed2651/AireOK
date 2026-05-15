package com.mohammed.aireok.presentation.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.useCase.auth.AuthUseCase
import com.mohammed.aireok.domain.useCase.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PerfilUiState {
    object Idle : PerfilUiState()
    object Loading : PerfilUiState()
    object Success : PerfilUiState()
    data class Error(val message: String) : PerfilUiState()
}

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {

    var nombre by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var perfilState by mutableStateOf<PerfilUiState>(PerfilUiState.Idle)
        private set

    init {
        viewModelScope.launch {
            authUseCase.currentUser.collect { user ->
                if (user != null && nombre.isBlank()) {
                    nombre = user.nombre
                    email = user.email
                }
            }
        }
    }

    fun actualizarPerfil(nuevoNombre: String, nuevoEmail: String) {
        viewModelScope.launch {
            perfilState = PerfilUiState.Loading
            try {
                val updated = userUseCase.actualizarPerfil(nuevoNombre, nuevoEmail)
                nombre = updated.nombre
                email = updated.email
                perfilState = PerfilUiState.Success
            } catch (e: Exception) {
                perfilState = PerfilUiState.Error("Error al actualizar el perfil")
            }
        }
    }

    fun resetPerfilState() {
        perfilState = PerfilUiState.Idle
    }
}

package com.mohammed.aireok.presentation.perfil

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.error.AppError
import com.mohammed.aireok.domain.useCase.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CambiarEmailUiState {
    object Idle : CambiarEmailUiState()
    object Loading : CambiarEmailUiState()
    object Success : CambiarEmailUiState()
    data class Error(val message: String) : CambiarEmailUiState()
}

@HiltViewModel
class CambiarEmailViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    var uiState by mutableStateOf<CambiarEmailUiState>(CambiarEmailUiState.Idle)
        private set

    fun cambiarEmail(email: String, password: String) {
        viewModelScope.launch {
            uiState = CambiarEmailUiState.Loading
            try {
                userUseCase.cambiarEmail(email.trim(), password)
                uiState = CambiarEmailUiState.Success
            } catch (e: Exception) {
                Log.e("CambiarEmailViewModel", "Error al cambiar email", e)
                uiState = CambiarEmailUiState.Error(mensajeDeError(e))
            }
        }
    }

    fun resetState() { uiState = CambiarEmailUiState.Idle }
}

private fun mensajeDeError(e: Exception): String = when (e) {
    is AppError.Unauthorized -> "Contraseña incorrecta"
    is AppError.Network      -> e.message ?: "Sin conexión a internet"
    is AppError.Timeout      -> "Tiempo de espera agotado"
    is AppError.Server       -> if (e.code == 409) "Ese email ya está en uso" else "Error del servidor"
    else                     -> "Error desconocido"
}

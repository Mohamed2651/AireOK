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

sealed class CambiarPasswordUiState {
    object Idle : CambiarPasswordUiState()
    object Loading : CambiarPasswordUiState()
    object Success : CambiarPasswordUiState()
    data class Error(val message: String) : CambiarPasswordUiState()
}

@HiltViewModel
class CambiarPasswordViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    var uiState by mutableStateOf<CambiarPasswordUiState>(CambiarPasswordUiState.Idle)
        private set

    fun cambiarPassword(passwordActual: String, passwordNueva: String) {
        viewModelScope.launch {
            uiState = CambiarPasswordUiState.Loading
            try {
                userUseCase.cambiarPassword(passwordActual, passwordNueva)
                uiState = CambiarPasswordUiState.Success
            } catch (e: Exception) {
                Log.e("CambiarPasswordViewModel", "Error al cambiar contraseña", e)
                uiState = CambiarPasswordUiState.Error(mensajeDeError(e))
            }
        }
    }

    fun resetState() { uiState = CambiarPasswordUiState.Idle }
}

private fun mensajeDeError(e: Exception): String = when (e) {
    is AppError.Unauthorized -> "Contraseña actual incorrecta"
    is AppError.Network      -> e.message ?: "Sin conexión a internet"
    is AppError.Timeout      -> "Tiempo de espera agotado"
    is AppError.Server       -> "Error del servidor"
    else                     -> "Error desconocido"
}

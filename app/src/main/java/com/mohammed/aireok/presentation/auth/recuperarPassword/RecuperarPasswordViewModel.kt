package com.mohammed.aireok.presentation.auth.recuperarPassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.error.AppError
import com.mohammed.aireok.domain.useCase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecuperarPasswordUiState {
    object Idle : RecuperarPasswordUiState()
    object Loading : RecuperarPasswordUiState()
    object Success : RecuperarPasswordUiState()
    data class Error(val message: String) : RecuperarPasswordUiState()
}

@HiltViewModel
class RecuperarPasswordViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    var uiState by mutableStateOf<RecuperarPasswordUiState>(RecuperarPasswordUiState.Idle)
        private set

    fun recuperarPassword(email: String) {
        viewModelScope.launch {
            uiState = RecuperarPasswordUiState.Loading
            uiState = try {
                authUseCase.recuperarPassword(email)
                RecuperarPasswordUiState.Success
            } catch (e: AppError) {
                RecuperarPasswordUiState.Error(
                    when (e) {
                        is AppError.Server -> when (e.code) {
                            404 -> "Servicio no disponible temporalmente. Inténtalo más tarde."
                            429 -> "Demasiados intentos. Espera unos minutos."
                            else -> "Error del servidor (${e.code}). Inténtalo de nuevo."
                        }
                        is AppError.Network -> "Sin conexión. Comprueba tu red e inténtalo de nuevo."
                        else -> "Error inesperado. Inténtalo de nuevo."
                    }
                )
            } catch (e: Exception) {
                RecuperarPasswordUiState.Error("Sin conexión. Comprueba tu red e inténtalo de nuevo.")
            }
        }
    }

    fun resetState() { uiState = RecuperarPasswordUiState.Idle }
}

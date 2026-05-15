package com.mohammed.aireok.presentation.auth.registro

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

sealed class RegistroUiState {
    object Idle : RegistroUiState()
    object Loading : RegistroUiState()
    object Success : RegistroUiState()
    data class Error(val message: String) : RegistroUiState()
}

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    var nombre by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var uiState by mutableStateOf<RegistroUiState>(RegistroUiState.Idle)
        private set

    fun updateNombre(v: String) { nombre = v }
    fun updateEmail(v: String) { email = v }
    fun updatePassword(v: String) { password = v }

    fun registro() {
        viewModelScope.launch {
            uiState = RegistroUiState.Loading
            uiState = try {
                authUseCase.registro(nombre, email, password)
                RegistroUiState.Success
            } catch (e: AppError) {
                RegistroUiState.Error(
                    when (e) {
                        is AppError.Server -> when (e.code) {
                            409 -> "Este email ya está registrado"
                            400 -> "Datos inválidos"
                            else -> "Error del servidor (${e.code})"
                        }
                        is AppError.Network -> "Sin conexión. Inténtalo de nuevo."
                        else -> e.message ?: "Error inesperado"
                    }
                )
            } catch (e: Exception) {
                RegistroUiState.Error("Sin conexión. Inténtalo de nuevo.")
            }
        }
    }

    fun resetState() {
        uiState = RegistroUiState.Idle
        nombre = ""
        email = ""
        password = ""
    }
}

package com.mohammed.aireok.presentation.auth.login

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

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val nombre: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun updateEmail(v: String) { email = v }
    fun updatePassword(v: String) { password = v }

    fun login() {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            uiState = try {
                val user = authUseCase.login(email, password)
                LoginUiState.Success(user.nombre)
            } catch (e: AppError) {
                LoginUiState.Error(
                    when (e) {
                        is AppError.Unauthorized -> "Email o contraseña incorrectos"
                        is AppError.Server -> when (e.code) {
                            400 -> "Email o contraseña incorrectos"
                            else -> "Error del servidor (${e.code})"
                        }
                        is AppError.Network -> "Sin conexión. Inténtalo de nuevo."
                        is AppError.Timeout -> "Tiempo de espera agotado. Inténtalo de nuevo."
                        else -> e.message ?: "Error inesperado"
                    }
                )
            } catch (e: Exception) {
                LoginUiState.Error("Sin conexión. Inténtalo de nuevo.")
            }
        }
    }

    fun resetState() {
        uiState = LoginUiState.Idle
        email = ""
        password = ""
    }
}

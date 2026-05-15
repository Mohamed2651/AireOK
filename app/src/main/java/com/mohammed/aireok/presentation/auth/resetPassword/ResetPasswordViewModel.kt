package com.mohammed.aireok.presentation.auth.resetPassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.error.AppError
import com.mohammed.aireok.domain.useCase.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ResetPasswordUiState {
    object Idle : ResetPasswordUiState()
    object Loading : ResetPasswordUiState()
    object Success : ResetPasswordUiState()
    data class Error(val message: String) : ResetPasswordUiState()
}

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tokenFromDeepLink: String = savedStateHandle.get<String>("token") ?: ""

    var uiState by mutableStateOf<ResetPasswordUiState>(ResetPasswordUiState.Idle)
        private set

    fun resetPassword(token: String, password: String) {
        viewModelScope.launch {
            uiState = ResetPasswordUiState.Loading
            uiState = try {
                val ok = authUseCase.resetPassword(token, password)
                if (ok) ResetPasswordUiState.Success
                else ResetPasswordUiState.Error("Token inválido o expirado. Solicita uno nuevo.")
            } catch (e: AppError) {
                ResetPasswordUiState.Error(
                    when (e) {
                        is AppError.Server -> when (e.code) {
                            400 -> "Token inválido o expirado. Solicita uno nuevo."
                            else -> "Error del servidor (${e.code})"
                        }
                        is AppError.Network -> "Sin conexión. Inténtalo de nuevo."
                        else -> e.message ?: "Error inesperado"
                    }
                )
            } catch (e: Exception) {
                ResetPasswordUiState.Error("Sin conexión. Inténtalo de nuevo.")
            }
        }
    }

    fun resetState() { uiState = ResetPasswordUiState.Idle }
}

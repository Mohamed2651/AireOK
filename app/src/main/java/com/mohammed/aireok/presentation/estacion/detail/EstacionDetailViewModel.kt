package com.mohammed.aireok.presentation.estacion.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import com.mohammed.aireok.domain.useCase.estacion.EstacionUseCase
import com.mohammed.aireok.domain.useCase.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EstacionDetailViewModel @Inject constructor(
    private val estacionUseCase: EstacionUseCase,
    private val userUseCase: UserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uid: String = savedStateHandle.get<String>("uid") ?: ""

    var estacion by mutableStateOf<EstacionEntity?>(null)
        private set
    var cargando by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    val favoritosFlow: StateFlow<Set<String>> = userUseCase.favoritosFlow

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                estacion = estacionUseCase.getEstacion(uid)
            } catch (e: Exception) {
                error = if (e is java.net.SocketTimeoutException)
                    "El servidor tardó demasiado en responder. Inténtalo de nuevo."
                else
                    "No se pudo cargar la estación"
            } finally {
                cargando = false
            }
        }
    }

    fun toggleFavorito(onResult: (ok: Boolean, seEstaAnadiendo: Boolean) -> Unit) {
        val esFav = uid in favoritosFlow.value
        val seAnade = !esFav
        viewModelScope.launch {
            try {
                if (esFav) userUseCase.eliminarFavorito(uid)
                else userUseCase.agregarFavorito(uid)
                onResult(true, seAnade)
            } catch (_: Exception) {
                onResult(false, seAnade)
            }
        }
    }
}

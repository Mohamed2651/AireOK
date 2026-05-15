package com.mohammed.aireok.presentation.favoritos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import com.mohammed.aireok.domain.useCase.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    var estaciones by mutableStateOf<List<EstacionEntity>>(emptyList())
        private set
    var cargando by mutableStateOf(false)
        private set

    init {
        cargarFavoritos()
    }

    fun cargarFavoritos() {
        viewModelScope.launch {
            cargando = true
            try {
                estaciones = userUseCase.obtenerFavoritos()
            } catch (_: Exception) {
            } finally {
                cargando = false
            }
        }
    }

    fun eliminarFavorito(idEstacion: String) {
        estaciones = estaciones.filter { it.id != idEstacion }
        viewModelScope.launch {
            try {
                userUseCase.eliminarFavorito(idEstacion)
            } catch (_: Exception) {
                cargarFavoritos()
            }
        }
    }
}

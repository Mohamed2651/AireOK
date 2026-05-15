package com.mohammed.aireok.presentation.estacion.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import com.mohammed.aireok.domain.useCase.estacion.EstacionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val estacionUseCase: EstacionUseCase
) : ViewModel() {

    var estaciones by mutableStateOf<List<EstacionEntity>>(emptyList())
        private set
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        cargarEstaciones()
    }

    fun cargarEstaciones() {
        if (cargando || estaciones.isNotEmpty()) return
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                estaciones = estacionUseCase.getEstaciones().take(20)
            } catch (e: Exception) {
                error = "No se pudieron cargar las estaciones"
            } finally {
                cargando = false
            }
        }
    }
}

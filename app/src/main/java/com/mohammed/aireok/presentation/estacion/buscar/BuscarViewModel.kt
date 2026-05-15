package com.mohammed.aireok.presentation.estacion.buscar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammed.aireok.domain.entity.estacion.EstacionBusquedaEntity
import com.mohammed.aireok.domain.useCase.estacion.EstacionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuscarViewModel @Inject constructor(
    private val estacionUseCase: EstacionUseCase
) : ViewModel() {

    var resultados by mutableStateOf<List<EstacionBusquedaEntity>>(emptyList())
        private set
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun buscar(query: String) {
        if (query.isBlank()) { limpiar(); return }
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                resultados = estacionUseCase.buscarEstacion(query)
            } catch (e: Exception) {
                error = "No se encontraron resultados"
                resultados = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    fun limpiar() {
        resultados = emptyList()
        error = null
    }
}

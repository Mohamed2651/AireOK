package com.mohammed.aireok.domain.useCase.estacion

import com.mohammed.aireok.domain.entity.estacion.EstacionBusquedaEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity

interface EstacionUseCase {
    suspend fun getEstaciones(): List<EstacionEntity>
    suspend fun getEstacion(uid: String): EstacionEntity
    suspend fun getEstacionCercana(lat: Double, lon: Double): EstacionEntity
    suspend fun buscarEstacion(query: String): List<EstacionBusquedaEntity>
}

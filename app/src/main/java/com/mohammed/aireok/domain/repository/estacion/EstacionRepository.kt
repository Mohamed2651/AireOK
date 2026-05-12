package com.mohammed.aireok.domain.repository.estacion

import com.mohammed.aireok.domain.entity.estacion.EstacionBusquedaEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity

interface EstacionRepository {
    suspend fun getEstaciones(): List<EstacionEntity>
    suspend fun getEstacion(uid: String): EstacionEntity
    suspend fun getEstacionCercana(lat: Double, lon: Double): EstacionEntity
    suspend fun buscarEstacion(query: String): List<EstacionBusquedaEntity>
}

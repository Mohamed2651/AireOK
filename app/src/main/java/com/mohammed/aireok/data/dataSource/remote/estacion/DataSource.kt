package com.mohammed.aireok.data.dataSource.remote.estacion

import com.mohammed.aireok.data.dataSource.remote.estacion.dto.BuscarEstacionDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto

interface DataSource {
    suspend fun getEstaciones(): List<EstacionDto>
    suspend fun getEstacion(uid: String): EstacionDto
    suspend fun getEstacionCercana(lat: Double, lon: Double): EstacionDto
    suspend fun buscarEstacion(query: String): List<BuscarEstacionDto>
}

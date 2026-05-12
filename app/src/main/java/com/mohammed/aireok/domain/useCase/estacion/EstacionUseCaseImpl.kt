package com.mohammed.aireok.domain.useCase.estacion

import com.mohammed.aireok.domain.entity.estacion.EstacionBusquedaEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import com.mohammed.aireok.domain.repository.estacion.EstacionRepository
import javax.inject.Inject

class EstacionUseCaseImpl @Inject constructor(
    private val repository: EstacionRepository
) : EstacionUseCase {

    override suspend fun getEstaciones(): List<EstacionEntity> = repository.getEstaciones()

    override suspend fun getEstacion(uid: String): EstacionEntity = repository.getEstacion(uid)

    override suspend fun getEstacionCercana(lat: Double, lon: Double): EstacionEntity =
        repository.getEstacionCercana(lat, lon)

    override suspend fun buscarEstacion(query: String): List<EstacionBusquedaEntity> =
        repository.buscarEstacion(query)
}

package com.mohammed.aireok.data.repository

import com.mohammed.aireok.data.dataSource.remote.estacion.DataSource
import com.mohammed.aireok.domain.common.ErrorHandler
import com.mohammed.aireok.domain.entity.estacion.EstacionBusquedaEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import com.mohammed.aireok.domain.repository.estacion.EstacionRepository
import javax.inject.Inject

class EstacionRepositoryImpl @Inject constructor(
    private val dataSource: DataSource,
    private val errorHandler: ErrorHandler
) : EstacionRepository {

    override suspend fun getEstaciones(): List<EstacionEntity> = try {
        dataSource.getEstaciones().map { it.toDomain() }
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun getEstacion(uid: String): EstacionEntity = try {
        dataSource.getEstacion(uid).toDomain()
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun getEstacionCercana(lat: Double, lon: Double): EstacionEntity = try {
        dataSource.getEstacionCercana(lat, lon).toDomain()
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun buscarEstacion(query: String): List<EstacionBusquedaEntity> = try {
        dataSource.buscarEstacion(query).map { it.toDomain() }
    } catch (e: Exception) { throw errorHandler.handle(e) }
}

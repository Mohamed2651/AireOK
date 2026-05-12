package com.mohammed.aireok.data.dataSource.remote.estacion

import com.mohammed.aireok.data.dataSource.remote.estacion.api.EstacionApi
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.BuscarEstacionDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto
import javax.inject.Inject

class DataSourceImpl @Inject constructor(
    private val api: EstacionApi
) : DataSource {

    override suspend fun getEstaciones(): List<EstacionDto> = api.getEstaciones()

    override suspend fun getEstacion(uid: String): EstacionDto = api.getEstacion(uid)

    override suspend fun getEstacionCercana(lat: Double, lon: Double): EstacionDto =
        api.getEstacionCercana(lat, lon)

    override suspend fun buscarEstacion(query: String): List<BuscarEstacionDto> =
        api.buscarEstacion(query)
}

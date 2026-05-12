package com.mohammed.aireok.data.repository

import com.mohammed.aireok.data.dataSource.remote.estacion.dto.BuscarEstacionDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.ContaminantesDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.ForecastDiaDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.ForecastDto
import com.mohammed.aireok.domain.entity.estacion.ContaminantesEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionBusquedaEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import com.mohammed.aireok.domain.entity.estacion.ForecastDiaEntity
import com.mohammed.aireok.domain.entity.estacion.ForecastEntity

fun EstacionDto.toDomain(): EstacionEntity = EstacionEntity(
    id = idEstacion?.takeIf { it.isNotBlank() && it != "undefined" } ?: "",
    nombre = nombre,
    lat = lat,
    lon = lon,
    ica = ica,
    nivel = nivel,
    color = color,
    dominante = dominante ?: contaminantePrincipal,
    contaminantes = contaminantes?.toDomain(),
    temperatura = temperatura,
    humedad = humedad,
    ultimaFecha = ultimaActualizacion ?: ultimaMedicion,
    datosFrescos = datosFrescos,
    forecast = forecast?.toDomain()
)

fun ContaminantesDto.toDomain(): ContaminantesEntity = ContaminantesEntity(
    pm25 = pm25, pm10 = pm10, no2 = no2, o3 = o3, so2 = so2, co = co
)

fun ForecastDiaDto.toDomain(): ForecastDiaEntity = ForecastDiaEntity(avg, day, max, min)

fun ForecastDto.toDomain(): ForecastEntity = ForecastEntity(
    o3 = o3?.map { it.toDomain() },
    pm10 = pm10?.map { it.toDomain() },
    pm25 = pm25?.map { it.toDomain() },
    uvi = uvi?.map { it.toDomain() }
)

fun BuscarEstacionDto.toDomain(): EstacionBusquedaEntity = EstacionBusquedaEntity(
    uid = uid, nombre = nombre, aqi = aqi, lat = lat, lon = lon
)

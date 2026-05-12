package com.mohammed.aireok.domain.entity.estacion

data class ContaminantesEntity(
    val pm25: Double?,
    val pm10: Double?,
    val no2: Double?,
    val o3: Double?,
    val so2: Double?,
    val co: Double?
)

data class ForecastDiaEntity(
    val avg: Double?,
    val day: String?,
    val max: Double?,
    val min: Double?
)

data class ForecastEntity(
    val o3: List<ForecastDiaEntity>?,
    val pm10: List<ForecastDiaEntity>?,
    val pm25: List<ForecastDiaEntity>?,
    val uvi: List<ForecastDiaEntity>?
)

data class EstacionEntity(
    val id: String,
    val nombre: String,
    val lat: Double?,
    val lon: Double?,
    val ica: Int?,
    val nivel: String?,
    val color: String?,
    val dominante: String?,
    val contaminantes: ContaminantesEntity?,
    val temperatura: Double?,
    val humedad: Double?,
    val ultimaFecha: String?,
    val datosFrescos: Boolean,
    val forecast: ForecastEntity? = null
)

data class EstacionBusquedaEntity(
    val uid: Int?,
    val nombre: String,
    val aqi: Int?,
    val lat: Double?,
    val lon: Double?
)

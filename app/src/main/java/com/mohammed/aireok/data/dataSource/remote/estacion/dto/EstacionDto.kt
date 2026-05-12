package com.mohammed.aireok.data.dataSource.remote.estacion.dto

import com.google.gson.annotations.SerializedName

data class ContaminantesDto(
    val pm25: Double? = null,
    val pm10: Double? = null,
    val no2: Double? = null,
    val o3: Double? = null,
    val so2: Double? = null,
    val co: Double? = null
)

data class ForecastDiaDto(
    val avg: Double? = null,
    val day: String? = null,
    val max: Double? = null,
    val min: Double? = null
)

data class ForecastDto(
    val o3: List<ForecastDiaDto>? = null,
    val pm10: List<ForecastDiaDto>? = null,
    val pm25: List<ForecastDiaDto>? = null,
    val uvi: List<ForecastDiaDto>? = null
)

data class EstacionDto(
    @SerializedName("id_estacion") val idEstacion: String? = null,
    val nombre: String = "",
    val lat: Double? = null,
    val lon: Double? = null,
    val ica: Int? = null,
    val nivel: String? = null,
    val color: String? = null,
    val dominante: String? = null,
    @SerializedName("contaminante_principal") val contaminantePrincipal: String? = null,
    val contaminantes: ContaminantesDto? = null,
    val temperatura: Double? = null,
    val humedad: Double? = null,
    @SerializedName("ultima_actualizacion") val ultimaActualizacion: String? = null,
    @SerializedName("ultima_medicion") val ultimaMedicion: String? = null,
    @SerializedName("datos_frescos") val datosFrescos: Boolean = false,
    val forecast: ForecastDto? = null
)

data class BuscarEstacionDto(
    val uid: Int? = null,
    val nombre: String = "",
    val aqi: Int? = null,
    val lat: Double? = null,
    val lon: Double? = null
)

package com.mohammed.aireok.network

import com.google.gson.annotations.SerializedName

data class LoginRequest(val email: String, val password: String)

data class RegistroRequest(val nombre: String, val email: String, val password: String)

data class UsuarioResponse(
    val id: Int? = null,
    val nombre: String = "",
    val email: String = "",
    val rol: String? = null
)

data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponse
)

data class EstacionBusquedaResponse(
    val uid: Int? = null,
    val nombre: String = "",
    val aqi: Int? = null,
    val lat: Double? = null,
    val lon: Double? = null
)

data class ContaminantesResponse(
    val pm25: Double? = null,
    val pm10: Double? = null,
    val no2: Double? = null,
    val o3: Double? = null,
    val so2: Double? = null,
    val co: Double? = null
)

data class ForecastDiaResponse(
    val avg: Double? = null,
    val day: String? = null,
    val max: Double? = null,
    val min: Double? = null
)

data class ForecastResponse(
    val o3: List<ForecastDiaResponse>? = null,
    val pm10: List<ForecastDiaResponse>? = null,
    val pm25: List<ForecastDiaResponse>? = null,
    val uvi: List<ForecastDiaResponse>? = null
)

// Usado tanto por /estaciones (lista) como por /estacion/:uid (detalle)
data class EstacionResponse(
    @SerializedName("id_estacion") val idEstacion: String? = null,
    val nombre: String = "",
    val lat: Double? = null,
    val lon: Double? = null,
    val ica: Int? = null,
    val nivel: String? = null,
    val color: String? = null,
    // detail usa "dominante", lista usa "contaminante_principal"
    val dominante: String? = null,
    @SerializedName("contaminante_principal") val contaminantePrincipal: String? = null,
    val contaminantes: ContaminantesResponse? = null,
    val temperatura: Double? = null,
    val humedad: Double? = null,
    // detail usa "ultima_actualizacion", lista usa "ultima_medicion"
    @SerializedName("ultima_actualizacion") val ultimaActualizacion: String? = null,
    @SerializedName("ultima_medicion") val ultimaMedicion: String? = null,
    @SerializedName("datos_frescos") val datosFrescos: Boolean = false,
    val forecast: ForecastResponse? = null
) {
    val resolvedUid: String get() = idEstacion?.takeIf { it.isNotBlank() && it != "undefined" } ?: ""
    val latDouble: Double? get() = lat
    val lonDouble: Double? get() = lon
    // Campo unificado: usa actualizacion (detail) o medicion (lista)
    val ultimaFecha: String? get() = ultimaActualizacion ?: ultimaMedicion
    // Campo unificado: usa dominante (detail) o contaminante_principal (lista)
    val dominanteEfectivo: String? get() = dominante ?: contaminantePrincipal
}

// Historial agrupado por día — GET /api/aire/historial/:uid?dias=7
data class HistorialDiaResponse(
    val fecha: String = "",
    @SerializedName("ica_promedio") val icaPromedio: Double? = null,
    @SerializedName("ica_max") val icaMax: Int? = null,
    @SerializedName("ica_min") val icaMin: Int? = null
)

// Preferencias de usuario — GET/PUT /api/usuario/preferencias
data class PreferenciasResponse(
    @SerializedName("estacion_favorita") val estacionFavorita: String? = null,
    @SerializedName("umbral_alerta") val umbralAlerta: Int? = null,
    @SerializedName("notificaciones_activas") val notificacionesActivas: Boolean = false
)

data class PreferenciasRequest(
    @SerializedName("estacion_favorita") val estacionFavorita: String? = null,
    @SerializedName("umbral_alerta") val umbralAlerta: Int? = null,
    @SerializedName("notificaciones_activas") val notificacionesActivas: Boolean = false
)

// Perfil — PUT /api/usuario/perfil
data class ActualizarPerfilRequest(val nombre: String)

// Favoritos — POST /api/usuario/favoritos
data class AgregarFavoritoRequest(
    @SerializedName("id_estacion") val idEstacion: String
)

// Recuperación de contraseña
data class RecuperarPasswordRequest(val email: String)
data class ResetPasswordRequest(val token: String, val password: String)
data class MensajeResponse(val mensaje: String)

// Incidencias — POST /api/incidencias, GET /api/incidencias/mis
data class IncidenciaRequest(
    val tipo: String,
    val descripcion: String,
    @SerializedName("estacion_relacionada") val estacionRelacionada: String? = null
)

data class IncidenciaResponse(
    val id: Int? = null,
    val tipo: String = "",
    val descripcion: String = "",
    val estado: String = "",
    @SerializedName("created_at") val createdAt: String? = null
)

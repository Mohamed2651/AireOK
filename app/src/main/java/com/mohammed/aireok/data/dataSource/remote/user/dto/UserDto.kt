package com.mohammed.aireok.data.dataSource.remote.user.dto

import com.google.gson.annotations.SerializedName

data class ActualizarPerfilRequestDto(val nombre: String, val email: String? = null)

data class AgregarFavoritoRequestDto(
    @SerializedName("id_estacion") val idEstacion: String
)

data class CambiarEmailRequestDto(val email: String, val password: String)

data class CambiarPasswordRequestDto(
    @SerializedName("password_actual") val passwordActual: String,
    @SerializedName("password_nueva") val passwordNueva: String
)

data class MensajeResponseDto(val mensaje: String)

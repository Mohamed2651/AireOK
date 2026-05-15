package com.mohammed.aireok.data.dataSource.remote.user.dto

import com.google.gson.annotations.SerializedName

data class ActualizarPerfilRequestDto(val nombre: String, val email: String? = null)

data class AgregarFavoritoRequestDto(
    @SerializedName("id_estacion") val idEstacion: String
)

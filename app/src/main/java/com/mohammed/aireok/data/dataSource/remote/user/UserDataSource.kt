package com.mohammed.aireok.data.dataSource.remote.user

import com.mohammed.aireok.data.dataSource.remote.auth.dto.UsuarioDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto

interface UserDataSource {
    suspend fun actualizarPerfil(nombre: String, email: String?): UsuarioDto
    suspend fun obtenerFavoritos(): List<EstacionDto>
    suspend fun agregarFavorito(idEstacion: String)
    suspend fun eliminarFavorito(idEstacion: String)
}

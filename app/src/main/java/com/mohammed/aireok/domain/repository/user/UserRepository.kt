package com.mohammed.aireok.domain.repository.user

import com.mohammed.aireok.domain.entity.auth.UserEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val favoritosFlow: StateFlow<Set<String>>
    suspend fun actualizarPerfil(nombre: String, email: String): UserEntity
    suspend fun obtenerFavoritos(): List<EstacionEntity>
    suspend fun agregarFavorito(idEstacion: String)
    suspend fun eliminarFavorito(idEstacion: String)
    suspend fun cambiarEmail(nuevoEmail: String, password: String)
    suspend fun cambiarPassword(passwordActual: String, passwordNueva: String)
}

package com.mohammed.aireok.domain.useCase.user

import com.mohammed.aireok.domain.entity.auth.UserEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import com.mohammed.aireok.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UserUseCaseImpl @Inject constructor(
    private val repository: UserRepository
) : UserUseCase {

    override val favoritosFlow: StateFlow<Set<String>> = repository.favoritosFlow

    override suspend fun actualizarPerfil(nombre: String, email: String): UserEntity =
        repository.actualizarPerfil(nombre, email)

    override suspend fun obtenerFavoritos(): List<EstacionEntity> = repository.obtenerFavoritos()

    override suspend fun agregarFavorito(idEstacion: String) = repository.agregarFavorito(idEstacion)

    override suspend fun eliminarFavorito(idEstacion: String) = repository.eliminarFavorito(idEstacion)
}

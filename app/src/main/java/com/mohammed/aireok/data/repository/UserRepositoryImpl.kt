package com.mohammed.aireok.data.repository

import com.mohammed.aireok.data.TokenManager
import com.mohammed.aireok.data.dataSource.remote.user.UserDataSource
import com.mohammed.aireok.domain.common.ErrorHandler
import com.mohammed.aireok.domain.entity.auth.UserEntity
import com.mohammed.aireok.domain.entity.estacion.EstacionEntity
import com.mohammed.aireok.domain.repository.user.UserRepository
import com.mohammed.aireok.network.AuthHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserDataSource,
    private val errorHandler: ErrorHandler,
    private val tokenManager: TokenManager
) : UserRepository {

    private val _favoritosFlow = MutableStateFlow<Set<String>>(emptySet())
    override val favoritosFlow: StateFlow<Set<String>> = _favoritosFlow.asStateFlow()

    override suspend fun actualizarPerfil(nombre: String, email: String): UserEntity = try {
        val emailParam = email.takeIf { it.isNotBlank() }
        val dto = dataSource.actualizarPerfil(nombre, emailParam)
        val user = dto.toDomain()
        tokenManager.saveSession(AuthHolder.token, user.nombre, user.email)
        user
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun obtenerFavoritos(): List<EstacionEntity> = try {
        val lista = dataSource.obtenerFavoritos().map { it.toDomain() }
        _favoritosFlow.value = lista.map { it.id }.toSet()
        lista
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun agregarFavorito(idEstacion: String) = try {
        _favoritosFlow.value = _favoritosFlow.value + idEstacion
        dataSource.agregarFavorito(idEstacion)
    } catch (e: Exception) {
        _favoritosFlow.value = _favoritosFlow.value - idEstacion
        throw errorHandler.handle(e)
    }

    override suspend fun eliminarFavorito(idEstacion: String) = try {
        _favoritosFlow.value = _favoritosFlow.value - idEstacion
        dataSource.eliminarFavorito(idEstacion)
    } catch (e: Exception) {
        _favoritosFlow.value = _favoritosFlow.value + idEstacion
        throw errorHandler.handle(e)
    }
}

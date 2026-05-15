package com.mohammed.aireok.data.repository

import com.mohammed.aireok.data.TokenManager
import com.mohammed.aireok.data.dataSource.remote.auth.AuthDataSource
import com.mohammed.aireok.domain.common.ErrorHandler
import com.mohammed.aireok.domain.entity.auth.UserEntity
import com.mohammed.aireok.domain.repository.auth.AuthRepository
import com.mohammed.aireok.network.AuthHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthDataSource,
    private val errorHandler: ErrorHandler,
    private val tokenManager: TokenManager
) : AuthRepository {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    override val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, password: String): UserEntity = try {
        val response = dataSource.login(email, password)
        val user = response.usuario.toDomain()
        tokenManager.saveSession(response.token, user.nombre, user.email)
        AuthHolder.token = response.token
        _currentUser.value = user
        user
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun registro(nombre: String, email: String, password: String) = try {
        dataSource.registro(nombre, email, password)
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun logout() {
        tokenManager.clearSession()
        AuthHolder.token = ""
        _currentUser.value = null
    }

    override suspend fun recuperarPassword(email: String) = try {
        dataSource.recuperarPassword(email)
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun resetPassword(token: String, password: String): Boolean = try {
        dataSource.resetPassword(token, password)
    } catch (e: Exception) { throw errorHandler.handle(e) }

    override suspend fun me(): UserEntity = try {
        val user = dataSource.me().toDomain()
        _currentUser.value = user
        user
    } catch (e: Exception) { throw errorHandler.handle(e) }
}

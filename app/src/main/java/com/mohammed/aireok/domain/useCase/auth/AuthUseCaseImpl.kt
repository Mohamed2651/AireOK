package com.mohammed.aireok.domain.useCase.auth

import com.mohammed.aireok.domain.entity.auth.UserEntity
import com.mohammed.aireok.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : AuthUseCase {

    override val currentUser: StateFlow<UserEntity?> = repository.currentUser

    override suspend fun login(email: String, password: String): UserEntity =
        repository.login(email, password)

    override suspend fun registro(nombre: String, email: String, password: String) =
        repository.registro(nombre, email, password)

    override suspend fun logout() = repository.logout()

    override suspend fun recuperarPassword(email: String) =
        repository.recuperarPassword(email)

    override suspend fun resetPassword(token: String, password: String): Boolean =
        repository.resetPassword(token, password)

    override suspend fun me(): UserEntity = repository.me()
}

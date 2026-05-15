package com.mohammed.aireok.domain.useCase.auth

import com.mohammed.aireok.domain.entity.auth.UserEntity
import kotlinx.coroutines.flow.StateFlow

interface AuthUseCase {
    val currentUser: StateFlow<UserEntity?>
    suspend fun login(email: String, password: String): UserEntity
    suspend fun registro(nombre: String, email: String, password: String)
    suspend fun logout()
    suspend fun recuperarPassword(email: String)
    suspend fun resetPassword(token: String, password: String): Boolean
    suspend fun me(): UserEntity
}

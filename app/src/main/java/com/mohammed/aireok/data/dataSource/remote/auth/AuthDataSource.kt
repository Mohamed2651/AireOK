package com.mohammed.aireok.data.dataSource.remote.auth

import com.mohammed.aireok.data.dataSource.remote.auth.dto.LoginResponseDto
import com.mohammed.aireok.data.dataSource.remote.auth.dto.UsuarioDto

interface AuthDataSource {
    suspend fun login(email: String, password: String): LoginResponseDto
    suspend fun registro(nombre: String, email: String, password: String)
    suspend fun recuperarPassword(email: String)
    suspend fun resetPassword(token: String, password: String): Boolean
    suspend fun me(): UsuarioDto
}

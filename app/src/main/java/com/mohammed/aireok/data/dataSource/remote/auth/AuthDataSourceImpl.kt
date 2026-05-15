package com.mohammed.aireok.data.dataSource.remote.auth

import com.mohammed.aireok.data.dataSource.remote.auth.api.AuthApi
import com.mohammed.aireok.data.dataSource.remote.auth.dto.*
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val api: AuthApi
) : AuthDataSource {

    override suspend fun login(email: String, password: String): LoginResponseDto =
        api.login(LoginRequestDto(email, password))

    override suspend fun registro(nombre: String, email: String, password: String) {
        api.registro(RegistroRequestDto(nombre, email, password))
    }

    override suspend fun recuperarPassword(email: String) {
        api.recuperarPassword(RecuperarPasswordRequestDto(email))
    }

    override suspend fun resetPassword(token: String, password: String): Boolean =
        api.resetPassword(ResetPasswordRequestDto(token, password)).isSuccessful

    override suspend fun me(): UsuarioDto = api.me()
}

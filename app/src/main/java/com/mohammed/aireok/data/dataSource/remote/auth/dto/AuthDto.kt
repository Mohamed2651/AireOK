package com.mohammed.aireok.data.dataSource.remote.auth.dto

data class LoginRequestDto(val email: String, val password: String)
data class RegistroRequestDto(val nombre: String, val email: String, val password: String)
data class UsuarioDto(
    val id: Int? = null,
    val nombre: String = "",
    val email: String = "",
    val rol: String? = null
)
data class LoginResponseDto(val token: String, val usuario: UsuarioDto)
data class RecuperarPasswordRequestDto(val email: String)
data class ResetPasswordRequestDto(val token: String, val password: String)

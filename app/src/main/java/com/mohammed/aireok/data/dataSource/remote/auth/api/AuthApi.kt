package com.mohammed.aireok.data.dataSource.remote.auth.api

import com.mohammed.aireok.data.dataSource.remote.auth.dto.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequestDto): UsuarioDto

    @POST("api/auth/recuperar")
    suspend fun recuperarPassword(@Body request: RecuperarPasswordRequestDto): Any

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<ResponseBody>

    @GET("api/auth/me")
    suspend fun me(): UsuarioDto
}

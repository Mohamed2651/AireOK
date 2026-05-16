package com.mohammed.aireok.data.dataSource.remote.user.api

import com.mohammed.aireok.data.dataSource.remote.auth.dto.UsuarioDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.ActualizarPerfilRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.AgregarFavoritoRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.CambiarEmailRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.CambiarPasswordRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.MensajeResponseDto
import retrofit2.http.*

interface UserApi {

    @PUT("api/usuario/perfil")
    suspend fun actualizarPerfil(@Body perfil: ActualizarPerfilRequestDto): UsuarioDto

    @PUT("api/usuario/cambiar-email")
    suspend fun cambiarEmail(@Body request: CambiarEmailRequestDto): MensajeResponseDto

    @PUT("api/usuario/cambiar-password")
    suspend fun cambiarPassword(@Body request: CambiarPasswordRequestDto): MensajeResponseDto

    @GET("api/usuario/favoritos")
    suspend fun obtenerFavoritos(): List<EstacionDto>

    @POST("api/usuario/favoritos")
    suspend fun agregarFavorito(@Body request: AgregarFavoritoRequestDto): Any

    @DELETE("api/usuario/favoritos/{id_estacion}")
    suspend fun eliminarFavorito(@Path("id_estacion") idEstacion: String): Any
}

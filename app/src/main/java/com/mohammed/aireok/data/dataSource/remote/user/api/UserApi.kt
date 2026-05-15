package com.mohammed.aireok.data.dataSource.remote.user.api

import com.mohammed.aireok.data.dataSource.remote.auth.dto.UsuarioDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.ActualizarPerfilRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.AgregarFavoritoRequestDto
import retrofit2.http.*

interface UserApi {

    @PUT("api/usuario/perfil")
    suspend fun actualizarPerfil(@Body perfil: ActualizarPerfilRequestDto): UsuarioDto

    @GET("api/usuario/favoritos")
    suspend fun obtenerFavoritos(): List<EstacionDto>

    @POST("api/usuario/favoritos")
    suspend fun agregarFavorito(@Body request: AgregarFavoritoRequestDto): Any

    @DELETE("api/usuario/favoritos/{id_estacion}")
    suspend fun eliminarFavorito(@Path("id_estacion") idEstacion: String): Any
}

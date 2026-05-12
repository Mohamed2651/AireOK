package com.mohammed.aireok.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequest): UsuarioResponse

    @POST("api/auth/recuperar")
    suspend fun recuperarPassword(@Body request: RecuperarPasswordRequest): MensajeResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResponseBody>

    @GET("api/auth/me")
    suspend fun me(): UsuarioResponse

    @GET("api/aire/estaciones")
    suspend fun estaciones(): List<EstacionResponse>

    @GET("api/aire/estacion/{uid}")
    suspend fun estacion(@Path("uid") uid: String): EstacionResponse

    @GET("api/aire/cercana")
    suspend fun estacionCercana(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): EstacionResponse

    @GET("api/aire/buscar")
    suspend fun buscarEstacion(@Query("q") query: String): List<EstacionBusquedaResponse>

    // ── Aire (con token) ──────────────────────────────────────────────────────

    @GET("api/aire/historial/{uid}")
    suspend fun historial(
        @Path("uid") uid: String,
        @Query("dias") dias: Int = 7
    ): List<HistorialDiaResponse>

    @POST("api/aire/sincronizar")
    suspend fun sincronizar(): Any

    // ── Usuario (con token) ───────────────────────────────────────────────────

    @GET("api/usuario/preferencias")
    suspend fun preferencias(): PreferenciasResponse

    @PUT("api/usuario/preferencias")
    suspend fun actualizarPreferencias(@Body preferencias: PreferenciasRequest): PreferenciasResponse

    @PUT("api/usuario/perfil")
    suspend fun actualizarPerfil(@Body perfil: ActualizarPerfilRequest): UsuarioResponse

    // ── Favoritos (con token) ─────────────────────────────────────────────────

    @GET("api/usuario/favoritos")
    suspend fun obtenerFavoritos(): List<EstacionResponse>

    @POST("api/usuario/favoritos")
    suspend fun agregarFavorito(@Body request: AgregarFavoritoRequest): Any

    @DELETE("api/usuario/favoritos/{id_estacion}")
    suspend fun eliminarFavorito(@Path("id_estacion") idEstacion: String): Any

    // ── Incidencias (con token) ───────────────────────────────────────────────

    @POST("api/incidencias")
    suspend fun reportarIncidencia(@Body incidencia: IncidenciaRequest): IncidenciaResponse

    @GET("api/incidencias/mis")
    suspend fun misIncidencias(): List<IncidenciaResponse>
}

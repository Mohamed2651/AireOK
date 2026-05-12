package com.mohammed.aireok.data.dataSource.remote.estacion.api

import com.mohammed.aireok.data.dataSource.remote.estacion.dto.BuscarEstacionDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EstacionApi {

    @GET("api/aire/estaciones")
    suspend fun getEstaciones(): List<EstacionDto>

    @GET("api/aire/estacion/{uid}")
    suspend fun getEstacion(@Path("uid") uid: String): EstacionDto

    @GET("api/aire/cercana")
    suspend fun getEstacionCercana(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): EstacionDto

    @GET("api/aire/buscar")
    suspend fun buscarEstacion(@Query("q") query: String): List<BuscarEstacionDto>
}

package com.mohammed.aireok.data.dataSource.remote.user

import com.mohammed.aireok.data.dataSource.remote.auth.dto.UsuarioDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto
import com.mohammed.aireok.data.dataSource.remote.user.api.UserApi
import com.mohammed.aireok.data.dataSource.remote.user.dto.ActualizarPerfilRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.AgregarFavoritoRequestDto
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val api: UserApi
) : UserDataSource {

    override suspend fun actualizarPerfil(nombre: String, email: String?): UsuarioDto =
        api.actualizarPerfil(ActualizarPerfilRequestDto(nombre, email))

    override suspend fun obtenerFavoritos(): List<EstacionDto> = api.obtenerFavoritos()

    override suspend fun agregarFavorito(idEstacion: String) {
        api.agregarFavorito(AgregarFavoritoRequestDto(idEstacion))
    }

    override suspend fun eliminarFavorito(idEstacion: String) {
        api.eliminarFavorito(idEstacion)
    }
}

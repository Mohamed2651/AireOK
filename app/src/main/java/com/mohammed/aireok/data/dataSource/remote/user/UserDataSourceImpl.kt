package com.mohammed.aireok.data.dataSource.remote.user

import com.mohammed.aireok.data.dataSource.remote.auth.dto.UsuarioDto
import com.mohammed.aireok.data.dataSource.remote.estacion.dto.EstacionDto
import com.mohammed.aireok.data.dataSource.remote.user.api.UserApi
import com.mohammed.aireok.data.dataSource.remote.user.dto.ActualizarPerfilRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.AgregarFavoritoRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.CambiarEmailRequestDto
import com.mohammed.aireok.data.dataSource.remote.user.dto.CambiarPasswordRequestDto
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

    override suspend fun cambiarEmail(nuevoEmail: String, password: String) {
        api.cambiarEmail(CambiarEmailRequestDto(nuevoEmail, password))
    }

    override suspend fun cambiarPassword(passwordActual: String, passwordNueva: String) {
        api.cambiarPassword(CambiarPasswordRequestDto(passwordActual, passwordNueva))
    }
}

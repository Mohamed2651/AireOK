package com.mohammed.aireok.data.repository

import com.mohammed.aireok.data.dataSource.remote.auth.dto.UsuarioDto
import com.mohammed.aireok.domain.entity.auth.UserEntity

fun UsuarioDto.toDomain(): UserEntity = UserEntity(
    id = id, nombre = nombre, email = email, rol = rol
)

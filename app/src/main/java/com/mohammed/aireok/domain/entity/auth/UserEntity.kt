package com.mohammed.aireok.domain.entity.auth

data class UserEntity(
    val id: Int?,
    val nombre: String,
    val email: String,
    val rol: String? = null
)

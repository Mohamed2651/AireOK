package com.mohammed.aireok.domain.error

sealed class AppError(message: String? = null) : Exception(message) {
    data class Network(override val message: String = "Sin conexión a internet") : AppError(message)
    data class Server(val code: Int, override val message: String = "Error del servidor") : AppError(message)
    object Unauthorized : AppError("Sesión expirada")
    object NotFound : AppError("Recurso no encontrado")
    object Timeout : AppError("Tiempo de espera agotado")
    data class Unknown(override val message: String = "Error desconocido") : AppError(message)
}

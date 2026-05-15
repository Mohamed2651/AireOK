package com.mohammed.aireok.domain.common

import com.mohammed.aireok.domain.error.AppError
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorHandler {

    fun handle(throwable: Throwable): AppError = when (throwable) {
        is AppError -> throwable
        is HttpException -> when (throwable.code()) {
            401 -> AppError.Unauthorized
            404 -> AppError.NotFound
            in 500..599 -> AppError.Server(throwable.code(), throwable.message())
            else -> AppError.Server(throwable.code(), throwable.message())
        }
        is SocketTimeoutException -> AppError.Timeout
        is IOException -> AppError.Network(throwable.message ?: "Sin conexión a internet")
        else -> AppError.Unknown(throwable.message ?: "Error desconocido")
    }
}

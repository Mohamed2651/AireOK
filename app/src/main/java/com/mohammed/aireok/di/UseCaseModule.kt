package com.mohammed.aireok.di

import com.mohammed.aireok.domain.useCase.auth.AuthUseCase
import com.mohammed.aireok.domain.useCase.auth.AuthUseCaseImpl
import com.mohammed.aireok.domain.useCase.estacion.EstacionUseCase
import com.mohammed.aireok.domain.useCase.estacion.EstacionUseCaseImpl
import com.mohammed.aireok.domain.useCase.user.UserUseCase
import com.mohammed.aireok.domain.useCase.user.UserUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindEstacionUseCase(impl: EstacionUseCaseImpl): EstacionUseCase

    @Binds
    abstract fun bindAuthUseCase(impl: AuthUseCaseImpl): AuthUseCase

    @Binds
    abstract fun bindUserUseCase(impl: UserUseCaseImpl): UserUseCase
}

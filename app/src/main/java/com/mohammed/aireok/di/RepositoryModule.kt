package com.mohammed.aireok.di

import com.mohammed.aireok.data.repository.AuthRepositoryImpl
import com.mohammed.aireok.data.repository.EstacionRepositoryImpl
import com.mohammed.aireok.data.repository.UserRepositoryImpl
import com.mohammed.aireok.domain.repository.auth.AuthRepository
import com.mohammed.aireok.domain.repository.estacion.EstacionRepository
import com.mohammed.aireok.domain.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindEstacionRepository(impl: EstacionRepositoryImpl): EstacionRepository

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

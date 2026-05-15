package com.mohammed.aireok.di

import com.mohammed.aireok.data.dataSource.remote.auth.AuthDataSource
import com.mohammed.aireok.data.dataSource.remote.auth.AuthDataSourceImpl
import com.mohammed.aireok.data.dataSource.remote.estacion.DataSource
import com.mohammed.aireok.data.dataSource.remote.estacion.DataSourceImpl
import com.mohammed.aireok.data.dataSource.remote.user.UserDataSource
import com.mohammed.aireok.data.dataSource.remote.user.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds @Singleton
    abstract fun bindEstacionDataSource(impl: DataSourceImpl): DataSource

    @Binds @Singleton
    abstract fun bindAuthDataSource(impl: AuthDataSourceImpl): AuthDataSource

    @Binds @Singleton
    abstract fun bindUserDataSource(impl: UserDataSourceImpl): UserDataSource
}

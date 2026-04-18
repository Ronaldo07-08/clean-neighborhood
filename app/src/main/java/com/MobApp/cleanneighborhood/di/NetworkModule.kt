package com.MobApp.cleanneighborhood.di

import com.MobApp.cleanneighborhood.data.network.ApiClient
import com.MobApp.cleanneighborhood.data.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiClient.apiService
    }
}
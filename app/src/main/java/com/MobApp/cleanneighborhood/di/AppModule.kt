package com.MobApp.cleanneighborhood.di

import android.content.Context
import com.MobApp.cleanneighborhood.data.UserLocationManager
import com.MobApp.cleanneighborhood.data.storage.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager {
        return TokenManager(context)
    }


    @Provides
    @Singleton
    fun provideUserLocationManager(
        @ApplicationContext context: Context
    ): UserLocationManager {
        return UserLocationManager(context)
    }


}
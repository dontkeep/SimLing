package com.doni.simling.hilt.modules

import com.doni.simling.models.connections.configs.ApiConfig
import com.doni.simling.models.connections.configs.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    fun provideApiConfig(): ApiConfig = ApiConfig()

    @Provides
    fun provideApiServices(apiConfig: ApiConfig): ApiServices = apiConfig.getApiService()
}

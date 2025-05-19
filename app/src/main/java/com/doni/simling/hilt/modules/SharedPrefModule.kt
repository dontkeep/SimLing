package com.doni.simling.hilt.modules

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefModule {
    //i dont think this is needed/a crucial part of the project, so, i'll delete it soon
}
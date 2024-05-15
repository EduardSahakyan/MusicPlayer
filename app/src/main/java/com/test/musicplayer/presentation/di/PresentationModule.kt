package com.test.musicplayer.presentation.di

import com.test.musicplayer.presentation.utils.AppDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PresentationModule {

    @Provides
    fun provideAppDispatchers() = AppDispatchers()

}
package com.test.musicplayer.data.di

import android.content.Context
import com.test.musicplayer.data.AudioRepositoryImpl
import com.test.musicplayer.domain.AudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAudioRepository(@ApplicationContext context: Context): AudioRepository = AudioRepositoryImpl(context)

}
package com.dogeby.tagplayer.data.di

import android.content.Context
import com.dogeby.tagplayer.data.video.source.local.VideoLocalDataSource
import com.dogeby.tagplayer.data.video.source.local.VideoLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoModule {

    @Singleton
    @Provides
    fun provideVideoLocalDataSource(@ApplicationContext context: Context): VideoLocalDataSource =
        VideoLocalDataSourceImpl(context.contentResolver)
}

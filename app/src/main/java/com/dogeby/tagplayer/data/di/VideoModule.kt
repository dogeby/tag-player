package com.dogeby.tagplayer.data.di

import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.data.video.VideoRepositoryImpl
import com.dogeby.tagplayer.data.video.source.local.VideoLocalDataSource
import com.dogeby.tagplayer.data.video.source.local.VideoLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoModule {

    @Singleton
    @Binds
    abstract fun bindsVideoLocalDataSource(videoLocalDataSourceImpl: VideoLocalDataSourceImpl): VideoLocalDataSource

    @Singleton
    @Binds
    abstract fun bindsVideoRepository(videoRepositoryImpl: VideoRepositoryImpl): VideoRepository
}

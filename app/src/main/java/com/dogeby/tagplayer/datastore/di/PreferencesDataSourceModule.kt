package com.dogeby.tagplayer.datastore.di

import com.dogeby.tagplayer.datastore.app.AppPreferencesDataSource
import com.dogeby.tagplayer.datastore.app.AppPreferencesDataSourceImpl
import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesDataSource
import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindsVideoListPreferencesDataSource(videoListPreferencesDataSourceImpl: VideoListPreferencesDataSourceImpl): VideoListPreferencesDataSource

    @Singleton
    @Binds
    abstract fun bindsAppPreferencesDataSource(appPreferencesDataSourceImpl: AppPreferencesDataSourceImpl): AppPreferencesDataSource
}

package com.dogeby.tagplayer.database.di

import com.dogeby.tagplayer.database.TagPlayerDatabase
import com.dogeby.tagplayer.database.dao.TagDao
import com.dogeby.tagplayer.database.dao.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    @Singleton
    fun providesTagDao(
        database: TagPlayerDatabase,
    ): TagDao = database.tagDao()

    @Provides
    @Singleton
    fun providesVideoDao(
        database: TagPlayerDatabase,
    ): VideoDao = database.videoDao()
}

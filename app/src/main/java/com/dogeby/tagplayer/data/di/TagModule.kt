package com.dogeby.tagplayer.data.di

import com.dogeby.tagplayer.data.tag.TagRepository
import com.dogeby.tagplayer.data.tag.TagRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TagModule {

    @Singleton
    @Binds
    abstract fun bindsTagRepository(tagRepositoryImpl: TagRepositoryImpl): TagRepository
}

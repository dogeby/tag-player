package com.dogeby.tagplayer.data.di

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import com.dogeby.tagplayer.data.preferences.PreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Singleton
    @Binds
    abstract fun bindsPreferencesRepository(preferencesRepositoryImpl: PreferencesRepositoryImpl): PreferencesRepository
}

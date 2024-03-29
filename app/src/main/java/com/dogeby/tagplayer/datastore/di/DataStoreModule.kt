package com.dogeby.tagplayer.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.dogeby.tagplayer.AppPreferences
import com.dogeby.tagplayer.VideoListPreferences
import com.dogeby.tagplayer.datastore.app.AppPreferencesSerializer
import com.dogeby.tagplayer.datastore.videolist.VideoListPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesVideoListPreferencesDataStore(
        @ApplicationContext appContext: Context,
        videoListPreferencesSerializer: VideoListPreferencesSerializer,
    ): DataStore<VideoListPreferences> =
        DataStoreFactory.create(
            serializer = videoListPreferencesSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        ) {
            appContext.dataStoreFile("video_list_preferences.pb")
        }

    @Provides
    @Singleton
    fun providesAppPreferencesDataStore(
        @ApplicationContext appContext: Context,
        appPreferencesSerializer: AppPreferencesSerializer,
    ): DataStore<AppPreferences> =
        DataStoreFactory.create(
            serializer = appPreferencesSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        ) {
            appContext.dataStoreFile("app_preferences.pb")
        }
}

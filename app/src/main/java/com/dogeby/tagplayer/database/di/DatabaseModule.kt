package com.dogeby.tagplayer.database.di

import android.content.Context
import androidx.room.Room
import com.dogeby.tagplayer.database.TagPlayerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesTagPlayerDatabase(
        @ApplicationContext context: Context,
    ): TagPlayerDatabase = Room.databaseBuilder(
        context,
        TagPlayerDatabase::class.java,
        "tag-player-database",
    ).build()
}

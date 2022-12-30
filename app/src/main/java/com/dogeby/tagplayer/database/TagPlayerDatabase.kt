package com.dogeby.tagplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dogeby.tagplayer.database.dao.TagDao
import com.dogeby.tagplayer.database.dao.TagVideoCrossRefDao
import com.dogeby.tagplayer.database.dao.VideoDao
import com.dogeby.tagplayer.database.model.TagEntity
import com.dogeby.tagplayer.database.model.TagVideoCrossRef
import com.dogeby.tagplayer.database.model.VideoEntity

@Database(entities = [TagEntity::class, VideoEntity::class, TagVideoCrossRef::class], version = 1)
abstract class TagPlayerDatabase : RoomDatabase() {

    abstract fun tagDao(): TagDao

    abstract fun videoDao(): VideoDao

    abstract fun tagVideoCrossRefDao(): TagVideoCrossRefDao
}

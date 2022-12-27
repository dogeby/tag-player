package com.dogeby.tagplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dogeby.tagplayer.database.dao.TagDao
import com.dogeby.tagplayer.database.model.TagEntity

@Database(entities = [TagEntity::class], version = 1)
abstract class TagPlayerDatabase : RoomDatabase() {

    abstract fun tagDao(): TagDao
}

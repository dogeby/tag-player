package com.dogeby.tagplayer.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dogeby.tagplayer.data.video.Video

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val duration: Int,
    @ColumnInfo(name = "parent_directory") val parentDirectory: String,
)

fun VideoEntity.toVideo() =
    Video(
        id = id,
        name = name,
        duration = duration,
        parentDirectory = parentDirectory,
    )

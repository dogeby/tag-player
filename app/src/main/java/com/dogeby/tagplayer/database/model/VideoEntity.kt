package com.dogeby.tagplayer.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dogeby.tagplayer.data.video.Video

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val duration: Int,
    val path: String,
)

fun VideoEntity.toVideo(uri: String) =
    Video(
        id = id,
        uri = uri,
        name = name,
        duration = duration,
        path = path,
    )

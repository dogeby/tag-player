package com.dogeby.tagplayer.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "tag_video_cross_refs",
    primaryKeys = ["tag_id", "video_id"],
)
data class TagVideoCrossRef(
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "video_id") val videoId: Long,
)

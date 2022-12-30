package com.dogeby.tagplayer.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "tag_video_cross_refs",
    primaryKeys = ["tag_id", "video_id"],
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = VideoEntity::class,
            parentColumns = ["id"],
            childColumns = ["video_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class TagVideoCrossRef(
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "video_id") val videoId: Long,
)

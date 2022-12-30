package com.dogeby.tagplayer.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class VideoEntityWithTagEntities(
    @Embedded val videoEntity: VideoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            TagVideoCrossRef::class,
            parentColumn = "video_id",
            entityColumn = "tag_id"
        ),
    )
    val tagEntities: List<TagEntity>,
)

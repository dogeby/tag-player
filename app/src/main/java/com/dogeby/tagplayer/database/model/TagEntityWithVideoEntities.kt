package com.dogeby.tagplayer.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagEntityWithVideoEntities(
    @Embedded val tagEntity: TagEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            TagVideoCrossRef::class,
            parentColumn = "tag_id",
            entityColumn = "video_id"
        ),
    )
    val videoEntities: List<VideoEntity>,
)

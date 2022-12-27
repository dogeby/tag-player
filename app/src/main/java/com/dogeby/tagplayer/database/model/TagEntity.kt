package com.dogeby.tagplayer.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dogeby.tagplayer.data.tag.Tag

@Entity(
    tableName = "tags",
    indices = [Index(value = ["name"], unique = true)],
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
)

fun TagEntity.toTag() =
    Tag(
        id = id,
        name = name
    )

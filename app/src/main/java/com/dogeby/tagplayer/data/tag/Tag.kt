package com.dogeby.tagplayer.data.tag

import com.dogeby.tagplayer.database.model.TagEntity

data class Tag(
    val id: Long = 0,
    val name: String,
) : Comparable<Tag> {

    override fun compareTo(other: Tag): Int {
        return id.compareTo(other.id)
    }
}

fun Tag.toTagEntity() =
    TagEntity(
        id = id,
        name = name,
    )

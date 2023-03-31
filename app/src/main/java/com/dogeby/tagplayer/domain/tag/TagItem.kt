package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.TagWithVideoIds

data class TagItem(
    val id: Long,
    val name: String,
    val imgUri: String?,
    val videoIds: List<Long>,
)

fun TagWithVideoIds.toTagItem(imgUri: String?): TagItem {
    return TagItem(
        id = tag.id,
        name = tag.name,
        imgUri = imgUri,
        videoIds = videoIds,
    )
}

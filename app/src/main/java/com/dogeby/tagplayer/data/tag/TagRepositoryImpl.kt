package com.dogeby.tagplayer.data.tag

import com.dogeby.tagplayer.database.dao.TagDao
import com.dogeby.tagplayer.database.dao.TagVideoCrossRefDao
import com.dogeby.tagplayer.database.model.toTag
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagVideoCrossRefDao: TagVideoCrossRefDao,
    private val tagDao: TagDao,
) : TagRepository {

    override val allTags: Flow<List<Tag>>
        get() = tagDao.getTagEntities().map { tagEntities -> tagEntities.map { it.toTag() } }

    override val tagsWithVideoIds: Flow<List<TagWithVideoIds>>
        get() = tagDao.getTagEntities()
            .map { tagEntities ->
                tagEntities.map { it.toTag() }
            }
            .combine(tagVideoCrossRefDao.getTagsWithVideos()) { tags, tagEntitiesWithVideoEntities ->
                val tagIdsWithVideoEntitiesMap = tagEntitiesWithVideoEntities.associateBy(
                    keySelector = { it.tagEntity.id },
                    valueTransform = { tagEntityWithVideoEntity ->
                        tagEntityWithVideoEntity.videoEntities.map { it.id }
                    },
                )
                tags.map { tag ->
                    TagWithVideoIds(tag, tagIdsWithVideoEntitiesMap[tag.id].orEmpty())
                }
            }

    override suspend fun addTags(tags: List<Tag>): Result<List<Long>> = runCatching {
        tagDao.insertTags(tags.map { it.toTagEntity() })
    }

    override suspend fun updateTags(tags: List<Tag>): Result<Int> = runCatching {
        tagDao.updateTags(tags.map { it.toTagEntity() })
    }

    override suspend fun deleteTags(ids: List<Long>): Result<Int> = runCatching {
        tagDao.deleteTags(ids)
    }

    override suspend fun deleteAllTags(): Result<Int> = runCatching {
        tagDao.deleteTags()
    }

    override fun getTags(ids: List<Long>): Flow<List<Tag>> {
        return tagDao.getTagEntities(ids).map { tagEntities -> tagEntities.map { it.toTag() } }
    }

    override fun findTags(nameKeyword: String): Flow<List<Tag>> {
        return tagDao.getTagEntities(nameKeyword).map { tagEntities -> tagEntities.map { it.toTag() } }
    }

    override suspend fun modifyTagName(id: Long, name: String): Result<Unit> = runCatching {
        tagDao.modifyTagName(id, name)
    }
}

package com.dogeby.tagplayer.data.tag

import com.dogeby.tagplayer.database.dao.TagDao
import com.dogeby.tagplayer.database.model.toTag
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override val allTags: Flow<List<Tag>>
        get() = tagDao.getTagEntities().map { tagEntities -> tagEntities.map { it.toTag() } }

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
}

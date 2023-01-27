package com.dogeby.tagplayer.data.tag

import kotlinx.coroutines.flow.Flow

interface TagRepository {

    val allTags: Flow<List<Tag>>

    suspend fun addTags(tags: List<Tag>): Result<List<Long>>

    suspend fun updateTags(tags: List<Tag>): Result<Int>

    suspend fun deleteTags(ids: List<Long>): Result<Int>

    suspend fun deleteAllTags(): Result<Int>

    fun getTags(ids: List<Long>): Flow<List<Tag>>

    fun findTags(nameKeyword: String): Flow<List<Tag>>
}

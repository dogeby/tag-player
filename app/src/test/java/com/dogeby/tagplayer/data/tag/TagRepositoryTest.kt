package com.dogeby.tagplayer.data.tag

import com.dogeby.tagplayer.database.dao.TagDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class TagRepositoryTest {

    @Mock private lateinit var tagDao: TagDao

    private lateinit var tagRepository: TagRepository

    @Before
    fun setUp() {
        tagRepository = TagRepositoryImpl(tagDao)
    }

    @Test
    fun insertTagAndGetAllTags() = runTest {
        val tags = List(5) { Tag(name = it.toString()) }
        val fakeTagEntities = tags.mapIndexed { index, tag -> tag.toTagEntity().copy(id = index.toLong()) }
        val fakeTagIds = fakeTagEntities.map { it.id }
        `when`(tagDao.insertTags(tags.map { it.toTagEntity() })).then {
            fakeTagIds
        }
        `when`(tagDao.getTagEntities()).then {
            flow {
                emit(fakeTagEntities)
            }
        }

        val addTagsResult = tagRepository.addTags(tags)
        val allTags = tagRepository.allTags.first()

        Assert.assertEquals(fakeTagIds, addTagsResult.getOrNull())
        Assert.assertEquals(tags.map { it.name }, allTags.map { it.name })
    }
}
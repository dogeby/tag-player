package com.dogeby.tagplayer.data.tag

import com.dogeby.tagplayer.database.dao.TagDao
import com.dogeby.tagplayer.database.dao.TagVideoCrossRefDao
import com.dogeby.tagplayer.database.model.TagEntityWithVideoEntities
import com.dogeby.tagplayer.database.model.VideoEntity
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

    @Mock private lateinit var tagVideoCrossRefDao: TagVideoCrossRefDao

    private lateinit var tagRepository: TagRepository

    @Before
    fun setUp() {
        tagRepository = TagRepositoryImpl(tagVideoCrossRefDao, tagDao)
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

    @Test
    fun getTagsWithVideoIds() = runTest {
        val tags = List(5) { Tag(it.toLong(), it.toString()) }
        val videoEntities = List(3) {
            VideoEntity(
                id = it.toLong(),
                name = "",
                extension = "",
                duration = 0L,
                path = "",
                size = 0L
            )
        }
        val tagEntitiesWithVideoEntities = tags.take(tags.size - 1).map { tag ->
            TagEntityWithVideoEntities(
                tagEntity = tag.toTagEntity(),
                videoEntities = videoEntities
            )
        }

        val expectedTagsWithVideoIds = tags.mapIndexed { index, tag ->
            TagWithVideoIds(tag, if (index < tags.size - 1) videoEntities.map { it.id } else emptyList())
        }

        `when`(tagDao.getTagEntities()).then {
            flow {
                emit(tags.map { it.toTagEntity() })
            }
        }
        `when`(tagVideoCrossRefDao.getTagsWithVideos()).then {
            flow {
                emit(tagEntitiesWithVideoEntities)
            }
        }

        val tagsWithVideoIds = tagRepository.tagsWithVideoIds.first()

        Assert.assertEquals(expectedTagsWithVideoIds, tagsWithVideoIds)
    }

    @Test
    fun getTagWithVideoIds() = runTest {
        val tag = Tag(0, "tag0")
        val videoEntities = List(3) {
            VideoEntity(
                id = it.toLong(),
                name = "",
                extension = "",
                duration = 0L,
                path = "",
                size = 0L
            )
        }
        val tagEntitiesWithVideoEntities = TagEntityWithVideoEntities(tag.toTagEntity(), videoEntities)
        val expectedTagWithVideoIds = TagWithVideoIds(tag, videoEntities.map { it.id })

        `when`(tagDao.getTagEntities(listOf(tag.id))).then {
            flow {
                emit(listOf(tag.toTagEntity()))
            }
        }
        `when`(tagVideoCrossRefDao.getTagsWithVideos(listOf(tag.id))).then {
            flow {
                emit(listOf(tagEntitiesWithVideoEntities))
            }
        }

        val tagWithVideoIds = tagRepository.getTagWithVideoIds(tag.id).first()

        Assert.assertEquals(expectedTagWithVideoIds, tagWithVideoIds.getOrNull())
    }

    @Test
    fun getTagWithVideoIds_videoEmpty() = runTest {
        val tag = Tag(0, "tag0")
        val expectedTagWithVideoIds = TagWithVideoIds(tag, emptyList(),)

        `when`(tagDao.getTagEntities(listOf(tag.id))).then {
            flow {
                emit(listOf(tag.toTagEntity()))
            }
        }
        `when`(tagVideoCrossRefDao.getTagsWithVideos(listOf(tag.id))).then {
            flow<List<TagEntityWithVideoEntities>> {
                emit(emptyList())
            }
        }

        val tagWithVideoIds = tagRepository.getTagWithVideoIds(tag.id).first()

        Assert.assertEquals(expectedTagWithVideoIds, tagWithVideoIds.getOrNull())
    }
}

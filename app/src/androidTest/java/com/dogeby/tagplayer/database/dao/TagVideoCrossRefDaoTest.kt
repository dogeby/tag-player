package com.dogeby.tagplayer.database.dao

import com.dogeby.tagplayer.database.model.TagEntity
import com.dogeby.tagplayer.database.model.TagVideoCrossRef
import com.dogeby.tagplayer.database.model.VideoEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class TagVideoCrossRefDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var tagDao: TagDao

    @Inject
    lateinit var videoDao: VideoDao

    @Inject
    lateinit var tagVideoCrossRefDao: TagVideoCrossRefDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun clear() {
        runBlocking {
            tagDao.deleteTags()
            videoDao.deleteVideos()
            tagVideoCrossRefDao.deleteTagVideoCrossRefs()
        }
    }

    @Test
    fun insertTagVideoCrossRefsAndGetTagsWithVideosWhereInId() = runTest {
        val tagEntities = List(5) { TagEntity(name = it.toString()) }
        val tagIds = tagDao.insertTags(tagEntities)
        val videoEntities = List(5) { VideoEntity(it.toLong(), it.toString(), it, it.toString()) }
        val videoIds = videoDao.insertVideos(videoEntities)
        val tagVideoCrossRefs = List(videoIds.size) { TagVideoCrossRef(tagIds.first(), videoIds[it]) }

        tagVideoCrossRefDao.insertTagVideoCrossRefs(tagVideoCrossRefs)
        val tagsWithVideos = tagVideoCrossRefDao.getTagsWithVideos(listOf(tagIds.first())).first()

        val tagEntitiesInDb = tagDao.getTagEntities().first()
        Assert.assertEquals(tagEntitiesInDb.first(), tagsWithVideos.first().tagEntity)
        val videoEntitiesInDb = videoDao.getVideoEntities().first()
        Assert.assertEquals(videoEntitiesInDb, tagsWithVideos.first().videoEntities)
    }

    @Test
    fun autoDeleteByForeignKeyOnDelete() = runTest {
        val tagEntities = List(5) { TagEntity(name = it.toString()) }
        val tagIds = tagDao.insertTags(tagEntities)
        val videoEntities = List(5) { VideoEntity(it.toLong(), it.toString(), it, it.toString()) }
        val videoIds = videoDao.insertVideos(videoEntities)
        val tagVideoCrossRefs = List(videoIds.size) { TagVideoCrossRef(tagIds.first(), videoIds[it]) }
        tagVideoCrossRefDao.insertTagVideoCrossRefs(tagVideoCrossRefs)

        tagDao.deleteTags(listOf(tagIds.first()))
        val tagVideoCrossRefsInDb = tagVideoCrossRefDao.getTagVideoCrossRefs().first()

        Assert.assertEquals(0, tagVideoCrossRefsInDb.size)
    }
}

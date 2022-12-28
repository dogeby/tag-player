package com.dogeby.tagplayer.database.dao

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
class VideoDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var videoDao: VideoDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun clear() {
        runBlocking { videoDao.deleteVideos() }
    }

    @Test
    fun cacheVideos_insertVideos() = runTest {
        val videoEntities = List(5) { VideoEntity(it.toLong(), it.toString(), it, it.toString()) }
        videoDao.insertVideos(videoEntities.dropLast(1))

        videoDao.cacheVideos(videoEntities)
        val videoEntitiesInDb = videoDao.getVideoEntities().first()

        Assert.assertEquals(videoEntities, videoEntitiesInDb)
    }

    @Test
    fun cacheVideos_updateVideos() = runTest {
        val videoEntities = List(5) { VideoEntity(it.toLong(), it.toString(), it, it.toString()) }
        val changedNameVideoEntities = videoEntities.map { it.copy(name = "new${it.name}") }
        videoDao.insertVideos(videoEntities)

        videoDao.cacheVideos(changedNameVideoEntities)
        val videoEntitiesInDb = videoDao.getVideoEntities().first()

        Assert.assertEquals(changedNameVideoEntities, videoEntitiesInDb)
    }

    @Test
    fun cacheVideos_deleteVideos() = runTest {
        val videoEntities = List(5) { VideoEntity(it.toLong(), it.toString(), it, it.toString()) }
        val newVideoEntities = videoEntities.drop(1)
        videoDao.insertVideos(videoEntities)

        videoDao.cacheVideos(newVideoEntities)
        val videoEntitiesInDb = videoDao.getVideoEntities().first()

        Assert.assertEquals(newVideoEntities, videoEntitiesInDb)
    }

    @Test
    fun deleteVideosNotInIds() = runTest {
        val videoEntities = List(5) { VideoEntity(it.toLong(), it.toString(), it, it.toString()) }
        val ids = videoDao.insertVideos(videoEntities)

        videoDao.deleteVideosNotInIds(listOf(ids.first()))
        val videoEntitiesInDb = videoDao.getVideoEntities().first()

        Assert.assertEquals(1, videoEntitiesInDb.count())
        Assert.assertEquals(ids.first(), videoEntitiesInDb.first().id)
    }
}

package com.dogeby.tagplayer.database.dao

import com.dogeby.tagplayer.database.model.TagEntity
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
class TagDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var tagDao: TagDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun clear() {
        runBlocking { tagDao.deleteTags() }
    }

    @Test
    fun insertTagsAndGetTagsOrderByName() = runTest {
        val tagEntities = List(5) { TagEntity(name = it.toString()) }.reversed()

        tagDao.insertTags(tagEntities)
        val tags = tagDao.getTagEntities().first()

        Assert.assertEquals(tagEntities[0].name, tags.last().name)
    }

    @Test
    fun insertTags_duplicateName() = runTest {
        val tagEntities = List(5) { TagEntity(name = it.toString()) }
        tagDao.insertTags(tagEntities)

        val result = tagDao.insertTags(listOf(TagEntity(name = tagEntities.first().name)))

        Assert.assertEquals(-1, result[0])
    }

    @Test
    fun updateTagsAndGetTags() = runTest {
        val tagEntities = List(5) { TagEntity(name = it.toString()) }
        val ids = tagDao.insertTags(tagEntities)
        val updateEntity = TagEntity(ids.first(), "-1")

        tagDao.updateTags(listOf(updateEntity))
        val tags = tagDao.getTagEntities().first()

        Assert.assertEquals(updateEntity.name, tags.find { it.id == ids.first() }?.name)
    }

    @Test
    fun updateTags_duplicateName() = runTest {
        val tagEntities = List(5) { TagEntity(name = it.toString()) }
        val ids = tagDao.insertTags(tagEntities)

        val result = tagDao.updateTags(listOf(TagEntity(ids.last(), tagEntities.first().name)))

        Assert.assertEquals(0, result)
    }

    @Test
    fun deleteTags() = runTest {
        val tagEntities = List(5) { TagEntity(name = it.toString()) }
        val ids = tagDao.insertTags(tagEntities)

        val result = tagDao.deleteTags(ids)

        Assert.assertEquals(ids.size, result)
    }

    @Test
    fun insertTagsAndGetTagsWhereInIds() = runTest {
        val tagEntities = List(5) { TagEntity(name = it.toString()) }

        val id = tagDao.insertTags(tagEntities).first()
        val tag = tagDao.getTagEntities(listOf(id)).first().first()

        Assert.assertEquals(tagEntities.first().copy(id = id), tag)
    }

    @Test
    fun findTags() = runTest {
        val keyword = "1"
        val tagEntities = List(20) { TagEntity(name = it.toString()) }
        val tagsWithKeywordInTheName = tagEntities.filter {
            it.name.contains(keyword)
        }
        tagDao.insertTags(tagEntities)

        val tagSearchResult = tagDao.getTagEntities(keyword).first()

        Assert.assertEquals(tagsWithKeywordInTheName.map { it.name }, tagSearchResult.map { it.name })
    }
}

package com.dogeby.tagplayer.datastore.videolist

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class VideoListPreferencesDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var videoListPreferencesDataSource: VideoListPreferencesDataSource

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun setFilteredTagIdsAndGetFilteredTagIds() = runTest {
        val tagIds = List(5) { it.toLong() }

        videoListPreferencesDataSource.setFilteredTagIds(tagIds)
        val data = videoListPreferencesDataSource.videoListPreferencesData.first()

        Assert.assertEquals(tagIds, data.filteredTagIds)
    }
}

package com.dogeby.tagplayer.data.video.source.local

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

@HiltAndroidTest
class VideoLocalDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var videoLocalDataSource: VideoLocalDataSource

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    /**
     * 테스트를 진행 할려면 에뮬레이터에 동영상 파일 1개 이상이 있어야한다.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getVideoDataList_afterUpdateVideoDataList() = runTest {
        videoLocalDataSource.updateVideoDataList()

        val videoDataList = videoLocalDataSource.videoDataList.first()

        Assert.assertNotEquals(0, videoDataList.size)
    }
}

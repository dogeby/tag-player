package com.dogeby.tagplayer.domain.tag

import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.data.video.Video
import com.dogeby.tagplayer.data.video.VideoRepository
import com.dogeby.tagplayer.data.video.VideoWithTags
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
class GetCommonTagsFromVideosUseCaseTest {

    @Mock private lateinit var videoRepository: VideoRepository

    lateinit var useCase: GetCommonTagsFromVideosUseCase

    @Before
    fun setUp() {
        useCase = GetCommonTagsFromVideosUseCase(videoRepository)
    }

    @Test
    fun getCommonTagsFromVideos() = runTest {
        val videoIds = List(5) { it.toLong() }
        val tags = List(5) { Tag(it.toLong(), it.toString()) }
        val expectedCommonTags = tags.take(2)
        val videosWithTags = videoIds.map {
            VideoWithTags(
                Video(it, it.toString(), it.toString(), it.toString(), it.toInt(), it.toString()),
                tags.take(it.toInt().coerceAtLeast(2))
            )
        }
        `when`(videoRepository.getVideosWithTags(videoIds)).then {
            flow {
                emit(videosWithTags)
            }
        }

        val commonTags = useCase(videoIds).first()

        Assert.assertEquals(expectedCommonTags, commonTags)
    }
}

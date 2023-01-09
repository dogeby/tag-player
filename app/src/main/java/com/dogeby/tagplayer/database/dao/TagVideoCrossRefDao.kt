package com.dogeby.tagplayer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dogeby.tagplayer.database.model.TagEntityWithVideoEntities
import com.dogeby.tagplayer.database.model.TagVideoCrossRef
import com.dogeby.tagplayer.database.model.VideoEntity
import com.dogeby.tagplayer.database.model.VideoEntityWithTagEntities
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

@Dao
abstract class TagVideoCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertTagVideoCrossRefs(tagVideoCrossRefs: List<TagVideoCrossRef>): List<Long>

    @Query(value = "DELETE FROM tag_video_cross_refs")
    abstract suspend fun deleteTagVideoCrossRefs(): Int

    @Query(value = "SELECT * FROM tag_video_cross_refs")
    abstract fun getTagVideoCrossRefs(): Flow<List<TagVideoCrossRef>>

    @Query(
        value = """
            SELECT DISTINCT video_id FROM tag_video_cross_refs
            WHERE tag_id IN (:tagIds)
        """
    )
    abstract fun getVideoIdsFilteredByTag(tagIds: List<Long>): Flow<List<Long>>

    @Transaction
    @Query(
        """
        SELECT * FROM tags
        ORDER BY name
    """
    )
    abstract fun getTagsWithVideos(): Flow<List<TagEntityWithVideoEntities>>

    @Transaction
    @Query(
        """
        SELECT * FROM tags
        WHERE id IN (:ids)
        ORDER BY name
    """
    )
    abstract fun getTagsWithVideos(ids: List<Long>): Flow<List<TagEntityWithVideoEntities>>

    @Transaction
    @Query(
        """
        SELECT * FROM videos
        ORDER BY name
    """
    )
    abstract fun getVideosWithTags(): Flow<List<VideoEntityWithTagEntities>>

    @Query(
        """
        SELECT * FROM videos
        ORDER BY name
    """
    )
    abstract fun getVideos(): Flow<List<VideoEntity>>

    open fun getVideosWithTagsFilteredNotByTag(): Flow<List<VideoEntityWithTagEntities>> {
        return getVideos().combine(getVideosWithTags()) { videos, videosWithTags ->
            val videosTags = videosWithTags.associateBy(
                keySelector = {
                    it.videoEntity.id
                },
                valueTransform = {
                    it.tagEntities
                }
            )

            videos.map {
                VideoEntityWithTagEntities(it, videosTags[it.id].orEmpty())
            }
        }
    }

    @Transaction
    @Query(
        """
        SELECT * FROM videos
        WHERE id IN (:ids)
        ORDER BY name
    """
    )
    abstract fun getVideosWithTags(ids: List<Long>): Flow<List<VideoEntityWithTagEntities>>

    @OptIn(ExperimentalCoroutinesApi::class)
    open fun getVideosWithTagsFilteredByTag(ids: List<Long>): Flow<List<VideoEntityWithTagEntities>> {
        return getVideoIdsFilteredByTag(ids).flatMapLatest { videoIds ->
            getVideosWithTags(videoIds)
        }
    }
}

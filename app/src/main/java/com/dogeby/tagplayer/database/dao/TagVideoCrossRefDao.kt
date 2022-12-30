package com.dogeby.tagplayer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dogeby.tagplayer.database.model.TagEntityWithVideoEntities
import com.dogeby.tagplayer.database.model.TagVideoCrossRef
import com.dogeby.tagplayer.database.model.VideoEntityWithTagEntities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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

    @Transaction
    @Query(
        """
        SELECT * FROM videos
        WHERE id IN (:ids)
        ORDER BY name
    """
    )
    abstract fun getVideosWithTags(ids: List<Long>): Flow<List<VideoEntityWithTagEntities>>

    open suspend fun getVideosWithTagsFilteredByTag(ids: List<Long>): Flow<List<VideoEntityWithTagEntities>> {
        val videoIds = getVideoIdsFilteredByTag(ids).first()
        return getVideosWithTags(videoIds)
    }
}
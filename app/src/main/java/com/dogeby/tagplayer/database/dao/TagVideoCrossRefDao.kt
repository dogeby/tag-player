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

@Dao
interface TagVideoCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagVideoCrossRefs(tagVideoCrossRefs: List<TagVideoCrossRef>): List<Long>

    @Query(value = "DELETE FROM tag_video_cross_refs")
    suspend fun deleteTagVideoCrossRefs(): Int

    @Query(value = "SELECT * FROM tag_video_cross_refs")
    fun getTagVideoCrossRefs(): Flow<List<TagVideoCrossRef>>

    @Transaction
    @Query(
        """
        SELECT * FROM tags
        ORDER BY name
    """
    )
    fun getTagsWithVideos(): Flow<List<TagEntityWithVideoEntities>>

    @Transaction
    @Query(
        """
        SELECT * FROM tags
        WHERE id IN (:ids)
        ORDER BY name
    """
    )
    fun getTagsWithVideos(ids: List<Long>): Flow<List<TagEntityWithVideoEntities>>

    @Transaction
    @Query(
        """
        SELECT * FROM videos
        ORDER BY name
    """
    )
    fun getVideosWithTags(): Flow<List<VideoEntityWithTagEntities>>

    @Transaction
    @Query(
        """
        SELECT * FROM videos
        WHERE id IN (:ids)
        ORDER BY name
    """
    )
    fun getVideosWithTags(ids: List<Long>): Flow<List<VideoEntityWithTagEntities>>
}
